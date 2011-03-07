/*
 * Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */

package uk.ac.ebi.gxa.index.builder.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.gxa.efo.Efo;
import uk.ac.ebi.gxa.index.builder.IndexAllCommand;
import uk.ac.ebi.gxa.index.builder.IndexBuilderException;
import uk.ac.ebi.gxa.index.builder.UpdateIndexForExperimentCommand;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.microarray.atlas.model.DesignElement;
import uk.ac.ebi.microarray.atlas.model.Gene;
import uk.ac.ebi.microarray.atlas.model.OntologyMapping;
import uk.ac.ebi.microarray.atlas.model.Property;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Iterables.partition;

/**
 * An {@link IndexBuilderService} that generates index documents from the genes in the Atlas database, and enriches the
 * data with expression values, links to EFO and other useful measurements.
 * <p/>
 * This is a heavily modified version of an original class first adapted to Atlas purposes by Pavel Kurnosov.
 * <p/>
 * Note that this implementation does NOT support updates - regardless of whether the update flag is set to true, this
 * will rebuild the index every time.
 *
 * @author Tony Burdett
 */
public class GeneAtlasIndexBuilderService extends IndexBuilderService {
    private Map<String, Collection<String>> ontomap =
            new HashMap<String, Collection<String>>();
    private Efo efo;
    private AtlasProperties atlasProperties;

    public void setAtlasProperties(AtlasProperties atlasProperties) {
        this.atlasProperties = atlasProperties;
    }

    public void setEfo(Efo efo) {
        this.efo = efo;
    }

    @Override
    public void processCommand(IndexAllCommand indexAll, ProgressUpdater progressUpdater) throws IndexBuilderException {
        super.processCommand(indexAll, progressUpdater);

        getLog().info("Indexing all genes...");
        indexGenes(progressUpdater, getAtlasDAO().getAllGenesFast());
    }

    @Override
    public void processCommand(UpdateIndexForExperimentCommand cmd, ProgressUpdater progressUpdater) throws IndexBuilderException {
        super.processCommand(cmd, progressUpdater);

        getLog().info("Indexing genes for experiment " + cmd.getAccession() + "...");
        indexGenes(progressUpdater, getAtlasDAO().getGenesByExperimentAccession(cmd.getAccession()));
    }

    private void indexGenes(final ProgressUpdater progressUpdater,
                            final List<Gene> genes) throws IndexBuilderException {
        java.util.Collections.shuffle(genes);

        final int total = genes.size();
        getLog().info("Found " + total + " genes to index");

        loadEfoMapping();

        final AtomicInteger processed = new AtomicInteger(0);
        final long timeStart = System.currentTimeMillis();

        final int fnothnum = atlasProperties.getGeneAtlasIndexBuilderNumberOfThreads();
        final int chunksize = atlasProperties.getGeneAtlasIndexBuilderChunksize();
        final int commitfreq = atlasProperties.getGeneAtlasIndexBuilderCommitfreq();

        getLog().info("Using " + fnothnum + " threads, " + chunksize + " chunk size, committing every " + commitfreq + " genes");
        ExecutorService tpool = Executors.newFixedThreadPool(fnothnum);
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>(genes.size());

        // index all genes in parallel
        for (final List<Gene> genelist : partition(genes, chunksize)) {
            // for each gene, submit a new task to the executor
            tasks.add(new Callable<Boolean>() {
                public Boolean call() throws IOException, SolrServerException {
                    try {
                        StringBuilder sblog = new StringBuilder();
                        long start = System.currentTimeMillis();

                        getAtlasDAO().getPropertiesForGenes(genelist);

                        List<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>(genelist.size());
                        for (Gene gene : genelist) {
                            SolrInputDocument solrInputDoc = createGeneSolrInputDocument(gene);

                            Set<String> designElements = new HashSet<String>();
                            for (DesignElement de : getAtlasDAO().getDesignElementsByGeneID(gene.getGeneID())) {
                                designElements.add(de.getName());
                                designElements.add(de.getAccession());
                            }
                            solrInputDoc.addField("property_designelement", designElements);
                            solrInputDoc.addField("properties", "designelement");

                            solrDocs.add(solrInputDoc);

                            int processedNow = processed.incrementAndGet();
                            if (processedNow % commitfreq == 0 || processedNow == total) {
                                long timeNow = System.currentTimeMillis();
                                long elapsed = timeNow - timeStart;
                                double speed = (processedNow / (elapsed / (double) commitfreq));  // (item/s)
                                double estimated = (total - processedNow) / (speed * 60);

                                getLog().info(
                                        String.format("Processed %d/%d genes %d%%, %.1f genes/sec overall, estimated %.1f min remaining",
                                                processedNow, total, (processedNow * 100 / total), speed, estimated));

                                progressUpdater.update(processedNow + "/" + total);
                            }
                            gene.clearProperties();
                        }

                        log(sblog, start, "adding genes to Solr index...");
                        getSolrServer().add(solrDocs);
                        log(sblog, start, "... batch complete.");
                        getLog().info("Gene chunk done:\n" + sblog);

                        return true;
                    } catch (RuntimeException e) {
                        getLog().error("Runtime exception occurred: " + e.getMessage(), e);
                        return false;
                    }
                }
            });
        }

        genes.clear();

        try {
            List<Future<Boolean>> results = tpool.invokeAll(tasks);
            Iterator<Future<Boolean>> iresults = results.iterator();
            while (iresults.hasNext()) {
                Future<Boolean> result = iresults.next();
                result.get();
                iresults.remove();
            }
        } catch (InterruptedException e) {
            getLog().error("Indexing interrupted!", e);
        } catch (ExecutionException e) {
            throw new IndexBuilderException("Error in indexing!", e.getCause());
        } finally {
            // shutdown the service
            getLog().info("Gene index building tasks finished, cleaning up resources and exiting");
            tpool.shutdown();
        }
    }

    private void log(StringBuilder sblog, long start, String message) {
        sblog.append("[ ").append(timestamp(start)).append(" ] ").append(message).append("\n");
    }

    private static long timestamp(long timeTaskStart) {
        return System.currentTimeMillis() - timeTaskStart;
    }

    private SolrInputDocument createGeneSolrInputDocument(final Gene gene) {
        // create a new solr document for this gene
        SolrInputDocument solrInputDoc = new SolrInputDocument();
        getLog().debug("Updating index with properties for " + gene.getIdentifier());

        // add the gene id field
        solrInputDoc.addField("id", gene.getGeneID());
        solrInputDoc.addField("species", gene.getSpecies());
        solrInputDoc.addField("name", gene.getName());
        solrInputDoc.addField("identifier", gene.getIdentifier());

        Set<String> propNames = new HashSet<String>();
        for (Property prop : gene.getProperties()) {
            String pv = prop.getValue();
            String p = prop.getName();
            if (pv == null)
                continue;
            if (p.toLowerCase().contains("ortholog")) {
                solrInputDoc.addField("orthologs", pv);
            } else {
                getLog().trace("Updating index, gene property " + p + " = " + pv);
                solrInputDoc.addField("property_" + p, pv);
                propNames.add(p);
            }
        }
        if (!propNames.isEmpty())
            solrInputDoc.setField("properties", propNames);

        getLog().debug("Properties for " + gene.getIdentifier() + " updated");

        return solrInputDoc;
    }


    private void loadEfoMapping() {
        getLog().info("Fetching ontology mappings...");

        // we don't support enything else yet
        List<OntologyMapping> mappings = getAtlasDAO().getOntologyMappingsByOntology("EFO");
        for (OntologyMapping mapping : mappings) {
            String mapKey = mapping.getExperimentId() + "_" +
                    mapping.getProperty() + "_" +
                    mapping.getPropertyValue();

            if (ontomap.containsKey(mapKey)) {
                // fetch the existing array and add this term
                // fixme: should actually add ontology term accession
                ontomap.get(mapKey).add(mapping.getOntologyTerm());
            } else {
                // add a new array
                Collection<String> values = new HashSet<String>();
                // fixme: should actually add ontology term accession
                values.add(mapping.getOntologyTerm());
                ontomap.put(mapKey, values);
            }
        }

        getLog().info("Ontology mappings loaded");
    }

    private static class UpDnSet {
        Set<Long> up = new HashSet<Long>();
        Set<Long> dn = new HashSet<Long>();
        Set<Long> no = new HashSet<Long>();
        Set<Long> childrenUp = new HashSet<Long>();
        Set<Long> childrenDn = new HashSet<Long>();
        Set<Long> childrenNo = new HashSet<Long>();
        boolean processed = false;
        float minpvalUp = 1;
        float minpvalDn = 1;
        float minpvalChildrenUp = 1;
        float minpvalChildrenDn = 1;

        void addChild(UpDnSet child) {
            childrenUp.addAll(child.childrenUp);
            childrenDn.addAll(child.childrenDn);
            childrenNo.addAll(child.childrenNo);
            childrenUp.addAll(child.up);
            childrenDn.addAll(child.dn);
            childrenNo.addAll(child.no);
            minpvalChildrenDn =
                    Math.min(Math.min(minpvalChildrenDn, child.minpvalChildrenDn),
                            child.minpvalDn);
            minpvalChildrenUp =
                    Math.min(Math.min(minpvalChildrenUp, child.minpvalChildrenUp),
                            child.minpvalUp);
        }
    }

    private static class UpDn {
        int cup = 0;
        int cdn = 0;
        int cno = 0;
        float pup = 1;
        float pdn = 1;
    }

    @Override
    public void finalizeCommand(UpdateIndexForExperimentCommand updateIndexForExperimentCommand, ProgressUpdater progressUpdater) throws IndexBuilderException {
        commit(); // do not optimize
    }

    public String getName() {
        return "genes";
    }
}
