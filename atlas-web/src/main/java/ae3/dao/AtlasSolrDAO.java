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

package ae3.dao;

import ae3.model.AtlasExperiment;
import ae3.model.AtlasGene;
import ae3.service.AtlasStatisticsQueryService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.gxa.statistics.Experiment;
import uk.ac.ebi.gxa.statistics.StatisticsType;
import uk.ac.ebi.gxa.utils.EscapeUtil;

import javax.annotation.Nonnull;
import java.util.*;

import static uk.ac.ebi.gxa.exceptions.LogUtil.logUnexpected;

/**
 * Atlas basic model elements access class
 *
 * @author ostolop, mdylag, pashky
 */
public class AtlasSolrDAO {
    private static final Logger log = LoggerFactory.getLogger(AtlasSolrDAO.class);

    private SolrServer solrServerAtlas;
    private SolrServer solrServerExpt;
    private AtlasStatisticsQueryService atlasStatisticsQueryService;

    public void setSolrServerAtlas(SolrServer solrServerAtlas) {
        this.solrServerAtlas = solrServerAtlas;
    }

    public void setSolrServerExpt(SolrServer solrServerExpt) {
        this.solrServerExpt = solrServerExpt;
    }

    public void setAtlasStatisticsQueryService(AtlasStatisticsQueryService atlasStatisticsQueryService) {
        this.atlasStatisticsQueryService = atlasStatisticsQueryService;
    }

    /**
     * Retrieve experiment by ID
     *
     * @param experiment_id_key experiment ID
     * @return experiment if found, null if not
     */
    public AtlasExperiment getExperimentById(String experiment_id_key) {
        return getExperimentByQuery("id:" + EscapeUtil.escapeSolr(experiment_id_key));
    }

    /**
     * Retrieve experiment by ID
     *
     * @param experiment_id_key experiment ID
     * @return experiment if found, null if not
     */
    public AtlasExperiment getExperimentById(long experiment_id_key) {
        return getExperimentById(String.valueOf(experiment_id_key));
    }

    /**
     * Returns an AtlasExperiment that contains all information from index.
     *
     * @param accessionId - an experiment accession/identifier.
     * @return an AtlasExperiment that contains all information from index.
     */
    public AtlasExperiment getExperimentByAccession(String accessionId) {
        return getExperimentByQuery("accession:" + EscapeUtil.escapeSolr(accessionId));
    }

    private AtlasExperiment getExperimentByQuery(String query) {
        SolrQuery q = new SolrQuery(query);
        q.setRows(1);
        q.setFields("*");
        try {
            QueryResponse queryResponse = solrServerExpt.query(q);
            SolrDocumentList documentList = queryResponse.getResults();

            if (documentList == null || documentList.size() < 1) {
                return null;
            }

            SolrDocument exptDoc = documentList.get(0);
            return AtlasExperiment.createExperiment(exptDoc);
        } catch (SolrServerException e) {
            throw logUnexpected("Error querying for experiment", e);
        }
    }

    /**
     * Experiment search results class
     */
    public static class AtlasExperimentsResult {
        private List<AtlasExperiment> experiments;
        private int totalResults;
        private int startingFrom;

        /**
         * Constructor
         *
         * @param experiments  list of experiments
         * @param totalResults total number of results
         * @param startingFrom start position of the list returned in full list of found results
         */
        private AtlasExperimentsResult(List<AtlasExperiment> experiments, int totalResults, int startingFrom) {
            this.experiments = experiments;
            this.totalResults = totalResults;
            this.startingFrom = startingFrom;
        }

        /**
         * Returns list of experiments
         *
         * @return list of experiments
         */
        public List<AtlasExperiment> getExperiments() {
            return experiments;
        }

        /**
         * Returns total number of found results
         *
         * @return total number of found results
         */
        public int getTotalResults() {
            return totalResults;
        }

        /**
         * Returns starting position of the list
         *
         * @return starting position of the list
         */
        public int getStartingFrom() {
            return startingFrom;
        }

        /**
         * Returns number of results in returned list
         *
         * @return number of results in returned list
         */
        public int getNumberOfResults() {
            return experiments.size();
        }
    }

    /**
     * Search experiments by SOLR query
     *
     * @param query SOLR query string
     * @param start starting position
     * @param rows  number of rows to fetch
     * @return experiments matching the query
     */
    public AtlasExperimentsResult getExperimentsByQuery(String query, int start, int rows) {
        SolrQuery q = new SolrQuery(query);
        q.setRows(rows);
        q.setStart(start);
        q.setFields("*");

        try {
            QueryResponse queryResponse = solrServerExpt.query(q);
            SolrDocumentList documentList = queryResponse.getResults();
            List<AtlasExperiment> result = new ArrayList<AtlasExperiment>();

            if (documentList != null)
                for (SolrDocument exptDoc : documentList)
                    result.add(AtlasExperiment.createExperiment(exptDoc));

            return new AtlasExperimentsResult(result, documentList == null ? 0 : (int) documentList.getNumFound(), start);
        } catch (SolrServerException e) {
            throw logUnexpected("Error querying for experiments", e);
        }
    }

    /**
     * List all experiments
     *
     * @return list of all experiments with UP/DOWN expressions
     */
    public Collection<AtlasExperiment> getExperiments() {

        Collection<Experiment> upDownScoringExperiments = atlasStatisticsQueryService.getScoringExperiments(StatisticsType.UP_DOWN);

        List<AtlasExperiment> result = new ArrayList<AtlasExperiment>();
        for (Experiment exp : upDownScoringExperiments) {
            AtlasExperiment atlasExp = getExperimentById(exp.getExperimentId());
            if (atlasExp != null)
                result.add(atlasExp);
            else
                throw logUnexpected("Failed to find experiment: " + exp + " in Solr experiment index!");
        }

        return result;
    }


    /**
     * Finds gene by id
     *
     * @param id gene id
     * @return atlas gene result
     */
    public AtlasGeneResult getGeneById(long id) {
        return getGeneByQuery("id:" + id);
    }

    public static class AtlasGeneResult {
        private AtlasGene gene;
        private boolean multi;

        private AtlasGeneResult(AtlasGene gene, boolean multi) {
            this.gene = gene;
            this.multi = multi;
        }

        public AtlasGene getGene() {
            return gene;
        }

        public boolean isMulti() {
            return multi;
        }

        public boolean isFound() {
            return gene != null;
        }
    }

    private AtlasGeneResult getGeneByQuery(String query) {
        SolrQuery q = new SolrQuery(query);
        q.setRows(1);
        q.setFields("*");
        try {
            QueryResponse queryResponse = solrServerAtlas.query(q);
            SolrDocumentList documentList = queryResponse.getResults();

            if (documentList == null || documentList.size() == 0) {
                return new AtlasGeneResult(null, false);
            }

            return new AtlasGeneResult(new AtlasGene(documentList.get(0)), documentList.getNumFound() > 1);
        } catch (SolrServerException e) {
            throw logUnexpected("Error querying for gene " + query, e);
        }
    }

    /**
     * Returns number of genes in index
     *
     * @return total number of indexed genes
     */
    public long getGeneCount() {
        final SolrQuery q = new SolrQuery("*:*");
        q.setRows(0);

        try {
            QueryResponse queryResponse = solrServerAtlas.query(q);
            SolrDocumentList documentList = queryResponse.getResults();

            return documentList.getNumFound();
        } catch (SolrServerException e) {
            throw logUnexpected("Error querying list of genes");
        }
    }

    /**
     * Returns genes that can be iterated
     *
     * @return Iterable<AtlasGene>
     */
    public Iterable<AtlasGene> getAllGenes() {
        final SolrQuery q = new SolrQuery("*:*");
        return createIteratorForQuery(q);
    }

    /**
     * Returns the AtlasGene corresponding to the specified gene identifier, i.e. matching one of the terms in the
     * "gene_ids" field in Solr schema.
     *
     * @param gene_identifier primary identifier
     * @return AtlasGene
     */
    public AtlasGeneResult getGeneByIdentifier(String gene_identifier) {
        final String id = EscapeUtil.escapeSolr(gene_identifier);
        return getGeneByQuery("id:" + id + " identifier:" + id);
    }

    /**
     * Searches gene by id (numerical), identifier (primary) or any of specified set of properties
     * supposedly containing other identifiers
     *
     * @param gene_identifier identifier
     * @param additionalIds   additional properties to search for
     * @return atlas gene search result
     */
    public AtlasGeneResult getGeneByAnyIdentifier(String gene_identifier, List<String> additionalIds) {
        final String id = EscapeUtil.escapeSolr(gene_identifier);
        StringBuilder sb = new StringBuilder("id:" + id + " identifier:" + id);
        for (String idprop : additionalIds)
            sb.append(" property_").append(idprop).append(":").append(id);
        return getGeneByQuery(sb.toString());
    }

    /**
     * Returns AtlasGenes corresponding to the specified gene identifiers, i.e. matching one of the terms in the
     * "gene_ids" field in Solr schema.
     *
     * @param ids Collection of ids
     * @return Iterable<AtlasGene>
     */
    public Iterable<AtlasGene> getGenesByIdentifiers(Collection ids) {
        if (ids.isEmpty()) return Collections.emptyList();

        StringBuilder sb = new StringBuilder();
        for (Object id : ids)
            sb.append(" id:").append(id).append(" identifier:").append(id);

        final SolrQuery q = new SolrQuery(sb.toString());
        return createIteratorForQuery(q);
    }

    /**
     * @param name  name of genes to search for
     * @return Iterable of AtlasGenes matching (gene) name in Solr gene index
     */
    public Iterable<AtlasGene> getGenesByName(String name) {
        final SolrQuery q = new SolrQuery(" name:" + name);
        return createIteratorForQuery(q);
    }

    private Iterable<AtlasGene> createIteratorForQuery(final SolrQuery q) {
        q.setRows(0);
        final long total;

        try {
            QueryResponse queryResponse = solrServerAtlas.query(q);
            SolrDocumentList documentList = queryResponse.getResults();

            total = documentList.getNumFound();
        } catch (SolrServerException e) {
            throw logUnexpected("Error querying list of genes");
        }

        return new Iterable<AtlasGene>() {
            public Iterator<AtlasGene> iterator() {
                return new Iterator<AtlasGene>() {
                    private Iterator<AtlasGene> genes = null;
                    private int totalSeen = 0;

                    public boolean hasNext() {
                        if (null == genes
                                ||
                                (!genes.hasNext() && totalSeen < total)) {
                            getNextGeneBatch();
                        }

                        return totalSeen < total && genes.hasNext();
                    }

                    public AtlasGene next() {
                        if (null == genes
                                ||
                                (!genes.hasNext() && totalSeen < total)) {
                            getNextGeneBatch();
                        }

                        totalSeen++;
                        return genes.next();
                    }

                    public void remove() {
                    }

                    private void getNextGeneBatch() {
                        try {
                            log.debug("Loading next batch of genes, seen " + totalSeen + " out of " + total);
                            List<AtlasGene> geneList = new ArrayList<AtlasGene>();

                            q.setRows(50);
                            q.setStart(totalSeen);

                            QueryResponse queryResponse = solrServerAtlas.query(q);
                            SolrDocumentList documentList = queryResponse.getResults();

                            for (SolrDocument d : documentList) {
                                AtlasGene g = new AtlasGene(d);
                                geneList.add(g);
                            }

                            genes = geneList.iterator();
                        } catch (SolrServerException e) {
                            throw logUnexpected("Error querying list of genes");
                        }

                    }
                };
            }
        };
    }


    /**
     * Fetch list of orthologs for specified gene
     *
     * @param atlasGene specified gene to look orthologs for
     * @return list of ortholog genes
     */
    public List<AtlasGene> getOrthoGenes(AtlasGene atlasGene) {
        List<AtlasGene> result = new ArrayList<AtlasGene>();
        for (String orth : atlasGene.getOrthologs()) {
            AtlasGeneResult orthoGene = getGeneByIdentifier(orth);
            if (orthoGene.isFound()) {
                result.add(orthoGene.getGene());
            }

            if (orthoGene.isMulti()) {
                log.info("Multiple genes found for ortholog " + orth + " of " + atlasGene.getGeneIdentifier());
            }
        }
        return result;
    }
}
