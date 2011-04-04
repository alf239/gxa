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

package uk.ac.ebi.gxa.requesthandlers.api.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ae3.dao.AtlasSolrDAO;
import ae3.model.AtlasGene;
import uk.ac.ebi.gxa.netcdf.reader.AtlasNetCDFDAO;
import uk.ac.ebi.gxa.netcdf.reader.NetCDFDescriptor;
import uk.ac.ebi.gxa.netcdf.reader.NetCDFProxy;

import java.util.*;
import java.io.IOException;

class DataQueryHandler implements QueryHandler {
    private Logger log = LoggerFactory.getLogger(getClass());

    private final AtlasSolrDAO atlasSolrDAO;
    private final AtlasNetCDFDAO atlasNetCDFDAO;

    DataQueryHandler(AtlasSolrDAO atlasSolrDAO, AtlasNetCDFDAO atlasNetCDFDAO) {
        this.atlasSolrDAO = atlasSolrDAO;
        this.atlasNetCDFDAO = atlasNetCDFDAO;
    }

    private static class GeneDataDecorator {
        final String name;
        final String id;
        final float[] expressionLevels;

        GeneDataDecorator(String name, String id, float[] expressionLevels) {
            this.name = name;
            this.id = id;
            this.expressionLevels = expressionLevels;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public float[] getExpressionLevels() {
            return expressionLevels;
        }
    }

    private static class DataDecorator {
        String[] assayAccessions;
        final List<GeneDataDecorator> genes = new LinkedList<GeneDataDecorator>();

        public String[] getAssayAccessions() {
            return assayAccessions;
        }

        public List<GeneDataDecorator> getGenes() {
            return genes;
        }
    }

    public Object getResponse(Map query) {
        Object value = query.get("experimentAccession");
        if (value == null) {
            return new Error("Experiment accession is not specified");
        } else if (!(value instanceof String)) {
            return new Error("Experiment accession must be a string");
        }
        final String experimentAccession = (String)value;

        value = query.get("assayAccessions");
        if (value == null) {
            return new Error("Assay accessions list is not specified");
        } else if (!(value instanceof List)) {
            return new Error("Assay accessions must be a list");
        }
        for (Object aa : (List)value) {
            if (!(aa instanceof String)) {
                return new Error("All assay accessions must be a strings");
            }
        }
        final List<String> assayAccessions = (List<String>)value;

        value = query.get("genes");
        if (value == null) {
            return new Error("Gene set is not specified");
        } else if (!(value instanceof List) && !"*".equals(value)) {
            return new Error("Gene set must be a list or \"*\" pattern");
        }
        if (value instanceof List) {
            for (Object g : (List)value) {
                if (!(g instanceof String)) {
                    return new Error("All assay accessions must be a strings");
                }
            }
        }
        final List<String> genes = (value instanceof List) ? (List<String>)value : null;

        try {
            final List<String> proxyIds = new LinkedList<String>();
            for (NetCDFDescriptor descriptor :
                atlasNetCDFDAO.getNetCDFProxiesForExperiment(experimentAccession)) {
                proxyIds.add(descriptor.getProxyId());
            }
            final Map<Long,AtlasGene> genesById;
            if (genes != null) {
                genesById = new TreeMap<Long,AtlasGene>();
                for (String geneName : genes) {
                    for (AtlasGene gene : atlasSolrDAO.getGenesByName(geneName)) {
                        genesById.put(gene.getGeneId(), gene);
                    }
                }
            } else {
                genesById = null;
            }
            final List<DataDecorator> data = new LinkedList<DataDecorator>();
            for (String pId : proxyIds) {
                final NetCDFProxy proxy = atlasNetCDFDAO.getNetCDFProxy(experimentAccession, pId);
                final Map<Integer,String> assayAccessionByIndex = new TreeMap<Integer,String>();
                int index = 0;
                for (String aa : proxy.getAssayAccessions()) {
                    if (assayAccessions.contains(aa)) {
                        assayAccessionByIndex.put(index, aa);
                    }
                    ++index;
                }
                final DataDecorator d = new DataDecorator();
                data.add(d);
                d.assayAccessions = new String[assayAccessionByIndex.size()];
                index = 0;
                for (String aa : assayAccessionByIndex.values()) {
                    d.assayAccessions[index++] = aa;
                }
                int deIndex = 0;
                for (Long geneId : proxy.getGenes()) {
                    if (genesById == null || genesById.keySet().contains(geneId)) {
                        final AtlasGene gene = genesById.get(geneId);
                        final GeneDataDecorator geneInfo = new GeneDataDecorator(
                            gene.getGeneName(),
                            gene.getGeneIdentifier(),
                            new float[assayAccessionByIndex.size()]
                        );
                        d.genes.add(geneInfo);
                        float[] levels = proxy.getExpressionDataForDesignElementAtIndex(deIndex);
                        index = 0;
                        for (int i : assayAccessionByIndex.keySet()) {
                            geneInfo.expressionLevels[index++] = levels[i];
                        }
                    }
                    ++deIndex;
                }
            }
            return data;
        } catch (IOException e) {
            return new Error(e.toString());
        }
    }
}
