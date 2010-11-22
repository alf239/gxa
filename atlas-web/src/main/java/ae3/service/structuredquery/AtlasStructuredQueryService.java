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

package ae3.service.structuredquery;

import ae3.dao.AtlasSolrDAO;
import ae3.model.*;
import ae3.service.AtlasStatisticsQueryService;
import com.google.common.collect.Multiset;
import org.apache.solr.common.params.FacetParams;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import uk.ac.ebi.gxa.index.GeneExpressionAnalyticsTable;
import uk.ac.ebi.gxa.efo.Efo;
import uk.ac.ebi.gxa.efo.EfoTerm;
import uk.ac.ebi.gxa.statistics.Attribute;
import uk.ac.ebi.gxa.statistics.GeneScore;
import uk.ac.ebi.gxa.statistics.StatisticsType;
import uk.ac.ebi.gxa.utils.*;
import uk.ac.ebi.gxa.index.builder.IndexBuilderEventHandler;
import uk.ac.ebi.gxa.index.builder.IndexBuilder;
import uk.ac.ebi.gxa.index.builder.listener.IndexBuilderEvent;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.microarray.atlas.model.ExpressionAnalysis;

import java.util.*;

/**
 * Structured query support class. The main query engine of the Atlas.
 *
 * @author pashky
 */
public class AtlasStructuredQueryService implements IndexBuilderEventHandler, DisposableBean {

    private static final int MAX_EFV_COLUMNS = 120;

    final private Logger log = LoggerFactory.getLogger(getClass());

    private SolrServer solrServerAtlas;
    private SolrServer solrServerExpt;
    private SolrServer solrServerProp;
    private AtlasProperties atlasProperties;
    private IndexBuilder indexBuilder;

    private AtlasEfvService efvService;
    private AtlasEfoService efoService;
    private AtlasGenePropertyService genePropService;
    private AtlasStatisticsQueryService atlasStatisticsQueryService;

    private AtlasSolrDAO atlasSolrDAO;

    private CoreContainer coreContainer;

    private Efo efo;

    private final Set<String> cacheFill = new HashSet<String>();
    private SortedSet<String> allSpecies = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Hack: prevents OOMs by clearing Lucene field cache by closing the searcher which closes the IndexReader
     * (it's the only way now if we don't hack Lucene)
     */
    private void controlCache() {
        if (coreContainer == null)
            return;

        synchronized (cacheFill) {
            if (cacheFill.size() > 500) {
                SolrCore core = coreContainer.getCore(Constants.CORE_ATLAS);
                if (core != null) {
                    core.closeSearcher();
                    core.close();
                }
                cacheFill.clear();
            }
        }
    }

    /**
     * Adds field to cache watcher (it's supposed to estimate number of fileds which actually end up in Lucene cache,
     * which we can't check directly)
     *
     * @param field
     */
    private void notifyCache(String field) {
        synchronized (cacheFill) {
            cacheFill.add(field);
        }
    }

    public SolrServer getSolrServerAtlas() {
        return solrServerAtlas;
    }

    public void setSolrServerAtlas(SolrServer solrServerAtlas) {
        this.solrServerAtlas = solrServerAtlas;
    }

    public SolrServer getSolrServerExpt() {
        return solrServerExpt;
    }

    public void setSolrServerExpt(SolrServer solrServerExpt) {
        this.solrServerExpt = solrServerExpt;
    }

    public SolrServer getSolrServerProp() {
        return solrServerProp;
    }

    public void setSolrServerProp(SolrServer solrServerProp) {
        this.solrServerProp = solrServerProp;
    }

    public CoreContainer getCoreContainer() {
        return coreContainer;
    }

    public void setCoreContainer(CoreContainer coreContainer) {
        this.coreContainer = coreContainer;
    }

    public AtlasEfvService getEfvService() {
        return efvService;
    }

    public void setEfvService(AtlasEfvService efvService) {
        this.efvService = efvService;
    }

    public AtlasEfoService getEfoService() {
        return efoService;
    }

    public void setEfoService(AtlasEfoService efoService) {
        this.efoService = efoService;
    }

    public AtlasSolrDAO getAtlasSolrDAO() {
        return atlasSolrDAO;
    }

    public void setAtlasSolrDAO(AtlasSolrDAO atlasSolrDAO) {
        this.atlasSolrDAO = atlasSolrDAO;
    }

    public void setIndexBuilder(IndexBuilder indexBuilder) {
        this.indexBuilder = indexBuilder;
        indexBuilder.registerIndexBuildEventHandler(this);
    }

    public Efo getEfo() {
        return efo;
    }

    public void setEfo(Efo efo) {
        this.efo = efo;
    }

    public void setGenePropService(AtlasGenePropertyService genePropService) {
        this.genePropService = genePropService;
    }

    public AtlasProperties getAtlasProperties() {
        return atlasProperties;
    }

    public void setAtlasProperties(AtlasProperties atlasProperties) {
        this.atlasProperties = atlasProperties;
    }

    public void setAtlasStatisticsQueryService(AtlasStatisticsQueryService atlasStatisticsQueryService) {
        this.atlasStatisticsQueryService = atlasStatisticsQueryService;
    }

    /**
     * SOLR query builder class. Collects necessary part of SOLR query string as we go through conditions.
     * Can't use just StringBuilder as we need to maintain two separate chains - query itself and scoring function.
     * <p/>
     * Can be used as chain of calls as all appendXX() methods return self
     */
    private static class SolrQueryBuilder {
        /**
         * Query string
         */
        private StringBuilder solrq = new StringBuilder();

        /**
         * Appends AND to query only if it is needed
         *
         * @return self
         */
        public SolrQueryBuilder appendAnd() {
            if (solrq.length() > 0)
                solrq.append(" AND ");
            return this;
        }

        /**
         * Appends string to query
         *
         * @param s string
         * @return self
         */
        public SolrQueryBuilder append(String s) {
            solrq.append(s);
            return this;
        }

        /**
         * Appends object to query
         *
         * @param s object
         * @return self
         */
        public SolrQueryBuilder append(Object s) {
            solrq.append(s);
            return this;
        }

        /**
         * Appends other SB to query
         *
         * @param s SB
         * @return self
         */
        public SolrQueryBuilder append(StringBuilder s) {
            solrq.append(s);
            return this;
        }

        /**
         * Returns assembled query string
         *
         * @return string
         */
        @Override
        public String toString() {
            return solrq.toString();
        }

        /**
         * Checks if query is empty
         *
         * @return true or false
         */
        public boolean isEmpty() {
            return solrq.length() == 0;
        }
    }

    /**
     * Column information class to be used as paylod in result EFV tree. Base version storing just position
     * of EFV data in result counters array
     */
    private static class BaseColumnInfo implements ColumnInfo {
        private int position;

        private BaseColumnInfo(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public int compareTo(ColumnInfo o) {
            return Integer.valueOf(getPosition()).compareTo(o.getPosition());
        }

        public boolean isQualified(UpdownCounter ud) {
            return !ud.isZero();
        }
    }

    /**
     * Extended version of columninfo, checking required minimum number of experiments
     */
    private static class QueryColumnInfo extends BaseColumnInfo {
        private int minUpExperiments = Integer.MAX_VALUE;
        private int minDnExperiments = Integer.MAX_VALUE;
        private int minOrExperiments = Integer.MAX_VALUE;
        private int minNoExperiments = Integer.MAX_VALUE;

        private QueryColumnInfo(int position) {
            super(position);
        }

        /**
         * Update column minimum requirements with provided query information
         * (to be called on each query condition)
         *
         * @param expression     query expression
         * @param minExperiments minimum number of experiments for this expression
         */
        public void update(QueryExpression expression, int minExperiments) {
            switch (expression) {
                case UP:
                case UP_ONLY:
                    minUpExperiments = Math.min(minExperiments, this.minUpExperiments);
                    break;
                case DOWN:
                case DOWN_ONLY:
                    minDnExperiments = Math.min(minExperiments, this.minDnExperiments);
                    break;
                case UP_DOWN:
                    minOrExperiments = Math.min(minExperiments, this.minOrExperiments);
                    break;
                case NON_D_E:
                    minNoExperiments = Math.min(minExperiments, this.minNoExperiments);
                    break;
            }
        }

        /**
         * Here it checks counter against minimal numbers
         *
         * @param ud counter
         * @return true or false
         */
        public boolean isQualified(UpdownCounter ud) {
            if (ud.getUps() >= minUpExperiments)
                return true;
            if (ud.getDowns() >= minDnExperiments)
                return true;
            if (ud.getNones() >= minNoExperiments)
                return true;
            if (ud.getUps() >= minOrExperiments || ud.getDowns() >= minOrExperiments)
                return true;
            return false;
        }
    }

    /**
     * Internal class to pass query state around methods (the main class itself is stateless hence thread-safe)
     */
    private class QueryState {
        private final SolrQueryBuilder solrq = new SolrQueryBuilder();
        private final EfvTree<ColumnInfo> efvs = new EfvTree<ColumnInfo>();
        private final EfoTree<ColumnInfo> efos = new EfoTree<ColumnInfo>(getEfo());
        private final Set<String> experiments = new HashSet<String>();

        private int num = 0;

        /**
         * Column numberer factory used to add new EFV columns into heatmap
         */
        private Maker<ColumnInfo> numberer = new Maker<ColumnInfo>() {
            public ColumnInfo make() {
                return new QueryColumnInfo(num++);
            }
        };

        /**
         * Returns SOLR query builder
         *
         * @return solr query builder
         */
        public SolrQueryBuilder getSolrq() {
            return solrq;
        }

        /**
         * Adds experiment IDs to query
         *
         * @param ids
         */
        public void addExperiments(Collection<String> ids) {
            experiments.addAll(ids);
        }

        /**
         * Adds EFV to query EFV tree
         *
         * @param ef             factor
         * @param efv            value
         * @param minExperiments required minimum number of experiments
         * @param expression     query expression
         */
        public void addEfv(String ef, String efv, int minExperiments, QueryExpression expression) {
            ((QueryColumnInfo) efvs.getOrCreate(ef, efv, numberer)).update(expression, minExperiments);
        }

        /**
         * Adds EFO accession to query EFO tree
         *
         * @param id             EFO accession
         * @param minExperiments required minimum number of experiments
         * @param expression     query expression
         */
        public void addEfo(String id, int minExperiments, QueryExpression expression) {
            for (ColumnInfo ci : efos.add(id, numberer, true))
                ((QueryColumnInfo) ci).update(expression, minExperiments);
        }

        /**
         * Returns set of experiments mentioned in the query
         *
         * @return set of experiment IDs
         */
        public Set<String> getExperiments() {
            return experiments;
        }

        /**
         * Returns query EFV tree
         *
         * @return query EFV tree
         */
        public EfvTree<ColumnInfo> getEfvs() {
            return efvs;
        }

        /**
         * Returns query EFO tree
         *
         * @return query EFO tree
         */
        public EfoTree<ColumnInfo> getEfos() {
            return efos;
        }

        /**
         * Checks if query is empty
         *
         * @return true or false
         */
        public boolean isEmpty() {
            return solrq.isEmpty();
        }

        /**
         * Checks if query has any condtion EFV/EFOs
         *
         * @return
         */
        public boolean hasQueryEfoEfvs() {
            return efvs.getNumEfvs() + efos.getNumEfos() > 0;
        }

        /**
         * Informative string representing the query
         *
         * @return string
         */
        @Override
        public String toString() {
            return "SOLR query: <" + solrq.toString() + ">, Experiments: [" + StringUtils.join(experiments, ", ") + "]";
        }
    }


    /**
     * Process structured Atlas query
     *
     * @param query parsed query
     * @return matching results
     * @throws java.io.IOException
     */
    public AtlasStructuredQueryResult doStructuredAtlasQuery(final AtlasStructuredQuery query) {
        final QueryState qstate = new QueryState();

        final Iterable<ExpFactorResultCondition> conditions = appendEfvsQuery(query, qstate);

        appendGeneQuery(query, qstate.getSolrq());
        appendSpeciesQuery(query, qstate.getSolrq());

        log.info("Structured query for " + query.getApiUrl() + ": " + qstate.toString());

        long timeStart = System.currentTimeMillis();
        AtlasStructuredQueryResult result = new AtlasStructuredQueryResult(query.getStart(), query.getRowsPerPage(), query.getExpsPerGene());
        log.info("AtlasStructuredQueryResult: " + (System.currentTimeMillis() - timeStart) + " ms");
        result.setConditions(conditions);

        if (!qstate.isEmpty()) {
            try {

                controlCache();

                timeStart = System.currentTimeMillis();
                SolrQuery q = setupSolrQuery(query, qstate);
                timeStart = System.currentTimeMillis();
                QueryResponse response = solrServerAtlas.query(q);
                log.info("Solr query took: " + (System.currentTimeMillis() - timeStart) + " ms");
                timeStart = System.currentTimeMillis();
                processResultGenes(response, result, qstate, query);
                log.info("processResultGenes took: " + (System.currentTimeMillis() - timeStart) + " ms");

                Set<String> expandableEfs = new HashSet<String>();
                EfvTree<ColumnInfo> trimmedEfvs = trimColumns(query, result, expandableEfs);
                result.setResultEfvs(trimmedEfvs);
                result.setExpandableEfs(expandableEfs);

                if (response.getFacetFields() != null) {
                    result.setEfvFacet(getEfvFacet(response, qstate));
                    for (String p : genePropService.getDrilldownProperties()) {
                        Set<String> hasVals = new HashSet<String>();
                        for (GeneQueryCondition qc : query.getGeneConditions())
                            if (qc.getFactor().equals(p))
                                hasVals.addAll(qc.getFactorValues());

                        Iterable<FacetCounter> facet = getGeneFacet(response, "property_f_" + p, hasVals);
                        if (facet.iterator().hasNext())
                            result.setGeneFacet(p, facet);
                    }
                    if (!query.getSpecies().iterator().hasNext())
                        result.setGeneFacet("species", getGeneFacet(response, "species", new HashSet<String>()));
                }
            } catch (SolrServerException e) {
                log.error("Error in structured query!", e);
            }
        }

        return result;
    }

    /**
     * Trims factors to contain only small amount of EFVs if too many of them were requested
     * User can ask to expand some of them
     *
     * @param query         query to process
     * @param result        result to process
     * @param expandableEfs which EFs to expand in result
     * @return trimmed result EFV tree
     */
    private EfvTree<ColumnInfo> trimColumns(final AtlasStructuredQuery query,
                                            final AtlasStructuredQueryResult result,
                                            Collection<String> expandableEfs) {
        final Set<String> expand = query.getExpandColumns();
        EfvTree<ColumnInfo> trimmedEfvs = new EfvTree<ColumnInfo>(result.getResultEfvs());
        if (expand.contains("*"))
            return trimmedEfvs;

        if (trimmedEfvs.getNumEfvs() < MAX_EFV_COLUMNS)
            return trimmedEfvs;


        int threshold = MAX_EFV_COLUMNS / trimmedEfvs.getNumEfs();

        for (EfvTree.Ef<ColumnInfo> ef : trimmedEfvs.getNameSortedTree()) {
            if (expand.contains(ef.getEf()) || ef.getEfvs().size() < threshold)
                continue;

            Map<EfvTree.Efv<ColumnInfo>, Double> scores = new HashMap<EfvTree.Efv<ColumnInfo>, Double>();
            for (EfvTree.Efv<ColumnInfo> efv : ef.getEfvs())
                scores.put(efv, 0.0);

            for (StructuredResultRow row : result.getResults()) {
                for (EfvTree.Efv<ColumnInfo> efv : ef.getEfvs()) {
                    UpdownCounter c = row.getCounters().get(efv.getPayload().getPosition());
                    scores.put(efv, scores.get(efv) + c.getDowns() * (1.0 - c.getMpvDn()) + c.getUps() * (1.0 - c.getMpvUp()));
                }
            }

            @SuppressWarnings("unchecked")
            Map.Entry<EfvTree.Efv<ColumnInfo>, Double>[] scoreset = scores.entrySet().toArray(new Map.Entry[1]);
            Arrays.sort(scoreset, new Comparator<Map.Entry<EfvTree.Efv<ColumnInfo>, Double>>() {
                public int compare(Map.Entry<EfvTree.Efv<ColumnInfo>, Double> o1, Map.Entry<EfvTree.Efv<ColumnInfo>, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            for (int i = threshold; i < scoreset.length; ++i) {
                trimmedEfvs.removeEfv(ef.getEf(), scoreset[i].getKey().getEfv());
                expandableEfs.add(ef.getEf());
            }
        }

        return trimmedEfvs;
    }

    /**
     * Finds experiment by search string
     *
     * @param query    search strings
     * @param condEfvs EFV tree to fill with EFVs mentioned in experiment
     * @return collection of found experiment IDs
     * @throws SolrServerException
     */
    private Collection<String> findExperiments(String query, EfvTree<Boolean> condEfvs) throws SolrServerException {

        List<String> result = new ArrayList<String>();
        if (query.length() == 0)
            return result;

        SolrQuery q = new SolrQuery("id:(" + query + ") accession:(" + query + ")");
        q.addField("*");
        q.setRows(50);
        q.setStart(0);

        QueryResponse qr = solrServerExpt.query(q);
        for (SolrDocument doc : qr.getResults()) {
            String id = String.valueOf(doc.getFieldValue("id"));
            if (id != null) {
                result.add(id);
                for (String name : doc.getFieldNames())
                    if (name.startsWith("a_property_"))
                        for (Object val : doc.getFieldValues(name))
                            condEfvs.put(name.substring("a_property_".length()), String.valueOf(val), true);
            }
        }

        return result;
    }

    /**
     * Returns experiment query part from provided IDs and query expression
     *
     * @param ids experiment IDs
     * @param e   query expression
     * @return string builder with query part to be fed to SolrQueryBuilder
     */
    private StringBuilder makeExperimentsQuery(Iterable<String> ids, QueryExpression e) {
        StringBuilder sb = new StringBuilder();
        String idss = StringUtils.join(ids.iterator(), " ");
        if (idss.length() == 0)
            return sb;
        switch (e) {
            case UP:
                sb.append("exp_up_ids:(").append(idss).append(") ");
                break;
            case DOWN:
                sb.append("exp_dn_ids:(").append(idss).append(") ");
                break;
            case UP_DOWN:
                sb.append("exp_ud_ids:(").append(idss).append(") ");
                break;
            case NON_D_E:
                sb.append("exp_no_ids:(").append(idss).append(") ");
                break;
        }
        return sb;
    }

    /**
     * Appends conditions part of the query to query state. Finds mathcing EFVs/EFOs and appends them to SOLR query string.
     *
     * @param query  query
     * @param qstate state
     * @return iterable conditions resulted from this append
     */
    private Iterable<ExpFactorResultCondition> appendEfvsQuery(final AtlasStructuredQuery query, final QueryState qstate) {
        final List<ExpFactorResultCondition> conds = new ArrayList<ExpFactorResultCondition>();
        SolrQueryBuilder solrq = qstate.getSolrq();

        for (ExpFactorQueryCondition c : query.getConditions()) {
            boolean isExperiment = Constants.EXP_FACTOR_NAME.equals(c.getFactor());
            if (c.isAnything() || (isExperiment && c.isAnyValue())) {
                // do nothing
            } else if (c.isOnly() && !c.isAnyFactor()
                    && !Constants.EFO_FACTOR_NAME.equals(c.getFactor())
                    && !Constants.EXP_FACTOR_NAME.equals(c.getFactor())) {
                try {
                    EfvTree<Boolean> condEfvs = getCondEfvsForFactor(c.getFactor(), c.getFactorValues());
                    EfvTree<Boolean> allEfvs = getCondEfvsAllForFactor(c.getFactor());
                    if (condEfvs.getNumEfs() + allEfvs.getNumEfs() > 0) {
                        // TODO solrq.appendAnd().append("((");
                        for (EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList()) {
                            // TODO solrq.append(" ");

                            String efefvId = condEfv.getEfEfvId();
                            // TODO solrq.appendExpFields(efefvId, c.getExpression(), c.getMinExperiments());
                            // TODO solrq.appendExpScores(efefvId, c.getExpression());

                            notifyCache(efefvId + c.getExpression());
                            qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                        }
                        // TODO solrq.append(")");
                        for (EfvTree.EfEfv<Boolean> allEfv : allEfvs.getNameSortedList())
                            if (!condEfvs.has(allEfv.getEf(), allEfv.getEfv())) {
                                String efefvId = allEfv.getEfEfvId();
                                // TODO solrq.append(" AND NOT (");
                                // TODO solrq.appendExpFields(efefvId, c.getExpression(), 1);
                                // TODO solrq.append(")");
                                notifyCache(efefvId + c.getExpression());
                                qstate.addEfv(allEfv.getEf(), allEfv.getEfv(), 1, QueryExpression.UP_DOWN);
                            }
                        // TODO solrq.append(")");
                        conds.add(new ExpFactorResultCondition(c,
                                Collections.<List<AtlasEfoService.EfoTermCount>>emptyList(),
                                false));
                    }
                } catch (SolrServerException e) {
                    log.error("Error querying Atlas index", e);
                }

            } else {
                try {
                    boolean nonemptyQuery = false;
                    EfvTree<Boolean> condEfvs = isExperiment ? new EfvTree<Boolean>() : getConditionEfvs(c);
                    if (condEfvs.getNumEfs() > 0) {
                        // TODO solrq.appendAnd().append("(");
                        int i = 0;
                        for (EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList()) {
                            if (++i > 100)
                                break;

                            // TODO solrq.append(" ");

                            String efefvId = condEfv.getEfEfvId();
                            // TODO solrq.appendExpFields(efefvId, c.getExpression(), c.getMinExperiments());
                            // TODO solrq.appendExpScores(efefvId, c.getExpression());

                            notifyCache(efefvId + c.getExpression());

                            if (Constants.EFO_FACTOR_NAME.equals(condEfv.getEf())) {
                                qstate.addEfo(condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                            } else {
                                qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                            }
                        }
                        // TODO solrq.append(")");
                        nonemptyQuery = true;
                    } else if (c.isAnyFactor() || isExperiment) {
                        // try to search for experiment too if no matching conditions are found
                        Collection<String> experiments = findExperiments(c.getSolrEscapedFactorValues(), condEfvs);
                        qstate.addExperiments(experiments);
                        StringBuilder expq = makeExperimentsQuery(experiments, c.getExpression());
                        if (expq.length() > 0) {
                            for (EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList()) {
                                qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                                // TODO solrq.appendExpScores(condEfv.getEfEfvId(), c.getExpression());
                            }
                            // TODO solrq.appendAnd().append(expq);
                            nonemptyQuery = true;
                        }
                    }
                    Collection<List<AtlasEfoService.EfoTermCount>> efoPaths = new ArrayList<List<AtlasEfoService.EfoTermCount>>();
                    Collection<EfvTree.Efv<Boolean>> condEfos = condEfvs.getEfvs(Constants.EFO_FACTOR_NAME);
                    for (EfvTree.Efv<Boolean> efv : condEfos) {
                        efoPaths.addAll(efoService.getTermParentPaths(efv.getEfv()));
                    }
                    conds.add(new ExpFactorResultCondition(c, efoPaths, !nonemptyQuery));
                } catch (SolrServerException e) {
                    log.error("Error querying Atlas index", e);
                }
            }
        }

        return conds;
    }

    /**
     * Appends gene part of the query. Parses query condtions and appends them to SOLR query string.
     *
     * @param query query
     * @param solrq solr query
     */
    private void appendGeneQuery(AtlasStructuredQuery query, SolrQueryBuilder solrq) {
        for (GeneQueryCondition geneQuery : query.getGeneConditions()) {
            solrq.appendAnd();
            if (geneQuery.isNegated())
                solrq.append(" NOT ");

            String escapedQ = geneQuery.getSolrEscapedFactorValues();
            if (geneQuery.isAnyFactor()) {
                solrq.append("(name:(").append(escapedQ).append(") species:(").append(escapedQ)
                        .append(") identifier:(").append(escapedQ).append(") id:(").append(escapedQ).append(")");
                for (String p : genePropService.getIdNameDescProperties())
                    solrq.append(" property_").append(p).append(":(").append(escapedQ).append(")");
                solrq.append(") ");
            } else if (Constants.GENE_PROPERTY_NAME.equals(geneQuery.getFactor())) {
                solrq.append("(name:(").append(escapedQ).append(") ");
                solrq.append("identifier:(").append(escapedQ).append(") ");
                solrq.append("id:(").append(escapedQ).append(") ");
                for (String nameProp : genePropService.getNameProperties())
                    solrq.append("property_" + nameProp + ":(").append(escapedQ).append(") ");
                solrq.append(")");
            } else if (genePropService.getDescProperties().contains(geneQuery.getFactor())
                    || genePropService.getIdProperties().contains(geneQuery.getFactor())) {
                String field = "property_" + geneQuery.getFactor();
                solrq.append(field).append(":(").append(escapedQ).append(")");
            }
        }
    }

    /**
     * Appends species part of the query to SOLR query
     *
     * @param query query
     * @param solrq solr query
     */
    private void appendSpeciesQuery(AtlasStructuredQuery query, SolrQueryBuilder solrq) {
        Set<String> species = new HashSet<String>();
        for (String s : query.getSpecies())
            for (String as : getSpeciesOptions())
                if (as.toLowerCase().contains(s.toLowerCase()))
                    species.add(as);

        if (!species.isEmpty()) {
            solrq.appendAnd().append("species:(").append(EscapeUtil.escapeSolrValueList(species)).append(")");
        }
    }

    /**
     * Returns tree of EFO/EFVs matching one specified query condition
     * EFOs are stored under "magic" factor named "efo"  at this point, they will go to EfoTree later
     * <p/>
     * This is dispatcher function calling one of specific for several query condtion cases. See the code.
     *
     * @param c condition
     * @return tree of EFVs/EFO
     * @throws SolrServerException
     */
    private EfvTree<Boolean> getConditionEfvs(QueryCondition c) throws SolrServerException {
        if (c.isAnyValue())
            return getCondEfvsAllForFactor(c.getFactor());

        if (c.isAnyFactor())
            return getCondEfvsForFactor(null, c.getFactorValues());

        return getCondEfvsForFactor(c.getFactor(), c.getFactorValues());
    }

    /**
     * Returns all EFVs/EFOs for specified factor
     *
     * @param factor factor
     * @return tree of EFVs/EFO
     */
    private EfvTree<Boolean> getCondEfvsAllForFactor(String factor) {
        EfvTree<Boolean> condEfvs = new EfvTree<Boolean>();
        if (Constants.EFO_FACTOR_NAME.equals(factor)) {
            Efo efo = getEfo();
            int i = 0;
            for (String v : efo.getRootIds()) {
                condEfvs.put(Constants.EFO_FACTOR_NAME, v, true);
                if (++i >= MAX_EFV_COLUMNS) {
                    break;
                }
            }
        } else {
            int i = 0;
            for (String v : efvService.listAllValues(factor)) {
                condEfvs.put(factor, v, true);
                if (++i >= MAX_EFV_COLUMNS) {
                    break;
                }
            }
        }
        return condEfvs;
    }

    /**
     * Returns matching EFVs/EFOs for factor
     *
     * @param factor factor
     * @param values values search strings
     * @return tree of EFVs/EFO
     * @throws SolrServerException
     */
    private EfvTree<Boolean> getCondEfvsForFactor(final String factor, final Iterable<String> values) throws SolrServerException {

        EfvTree<Boolean> condEfvs = new EfvTree<Boolean>();

        if (Constants.EFO_FACTOR_NAME.equals(factor) || null == factor) {
            Efo efo = getEfo();
            for (String v : values) {
                for (EfoTerm term : efo.searchTerm(EscapeUtil.escapeSolr(v))) {
                    condEfvs.put(Constants.EFO_FACTOR_NAME, term.getId(), true);
                }
            }
        }

        if (Constants.EFO_FACTOR_NAME.equals(factor))
            return condEfvs;

        String queryString = EscapeUtil.escapeSolrValueList(values);
        if (factor != null)
            queryString = "(" + queryString + ") AND property:" + EscapeUtil.escapeSolr(factor);

        SolrQuery q = new SolrQuery(queryString);
        q.setRows(10000);
        q.setStart(0);
        q.setFields("*");

        QueryResponse qr = solrServerProp.query(q);

        for (SolrDocument doc : qr.getResults()) {
            String ef = (String) doc.getFieldValue("property");
            String efv = (String) doc.getFieldValue("value");
            condEfvs.put(ef, efv, true);
        }
        return condEfvs;
    }


    /**
     * This method returns a local cache to avoid re-loading bit stats for a given efo.efv term in
     * consecutive heat map rows
     * @return Map: stat type -> Map: efo/efv -> Multiset<Integer> of aggregate scores for gene indexes
     */
    private Map<StatisticsType, HashMap<String, Multiset<Integer>>> getScoresCache() {

        Map<StatisticsType, HashMap<String, Multiset<Integer>>> statTypeToEfoToScores
                = new HashMap<StatisticsType, HashMap<String, Multiset<Integer>>>();
        Set<StatisticsType> statTypesToBeCached = new HashSet<StatisticsType>();

        statTypesToBeCached.add(StatisticsType.UP);
        statTypesToBeCached.add(StatisticsType.DOWN);
        statTypesToBeCached.add(StatisticsType.NON_D_E);
        for (StatisticsType statisticsType : statTypesToBeCached) {
            statTypeToEfoToScores.put(statisticsType, new HashMap<String, Multiset<Integer>>());
        }
        return statTypeToEfoToScores;
    }

    /**
     * Add scores to cascoresCache, under statType-> efoOrEfv
     * @param scoresCache
     * @param scores
     * @param statType
     * @param efoOrEfv
     */
    private void addToScoresCache(
            Map<StatisticsType, HashMap<String, Multiset<Integer>>> scoresCache,
            Multiset<Integer> scores,
            StatisticsType statType,
            String efoOrEfv) {
        if (!scoresCache.get(statType).containsKey(efoOrEfv)) {
            scoresCache.get(statType).put(efoOrEfv, scores);
        }
    }

    /**
     *
     * @param scoresCache
     * @param statType
     * @param efoOrEfv
     * @return  Multiset<Integer> of aggregate scores for gene indexes stored in cache under statType-> efoOrEfv
     */
    private Multiset<Integer> getScoresFromCache(
            Map<StatisticsType, HashMap<String, Multiset<Integer>>> scoresCache,
            StatisticsType statType,
            String efoOrEfv) {
            return scoresCache.get(statType).get(efoOrEfv);
    }

    /**
     *
     * @param scoresCache - cache that stores experiment counts for geneIndexes - if it doesn't contain the required count, populate it. geneIndexes contains indexes of all genes of interest for the current query
     * (including geneId)
     * @param efvOrEfo
     * @param statType
     * @param isEfo flag indicating if efvOrEfo is an efo term (true)
     * @param geneId
     * @param geneIndexes
     * @return experiment count for statType, efvOrEfo, geneId
     */
    private int getExperimentCountsForGene(Map<StatisticsType, HashMap<String, Multiset<Integer>>> scoresCache, String efvOrEfo, StatisticsType statType, boolean isEfo, Long geneId, Set<Integer> geneIndexes) {
        Multiset<Integer> scores = getScoresFromCache(scoresCache, statType, efvOrEfo);
        Integer geneIndex = atlasStatisticsQueryService.getIndexForGene(geneId);
        if (scores == null) {
            scores = atlasStatisticsQueryService.getExperimentCounts(new Attribute(efvOrEfo, isEfo), statType, geneIndexes);
            addToScoresCache(scoresCache, scores, statType, efvOrEfo);
        }
        return scores.count(geneIndex);
    }


    /**
     * Processes SOLR query response and generates Atlas structured query result
     *
     * @param response SOLR response
     * @param result   ATlas result
     * @param qstate   query state
     * @param query    query itself
     * @throws SolrServerException
     */
    private void processResultGenes(QueryResponse response,
                                    AtlasStructuredQueryResult result,
                                    QueryState qstate, AtlasStructuredQuery query) throws SolrServerException {


        // Initialise scores cache to store efo counts for the group of genes of interest to this query.
        // For each heat map row other than the first, the cache will be hit instead of AtlasStatisticsQueryService
        Map<StatisticsType, HashMap<String, Multiset<Integer>>> scoresCache = getScoresCache();

        SolrDocumentList docs = response.getResults();
        SortedMap<GeneScore, StructuredResultRow> geneScoreToResultRow = new TreeMap<GeneScore, StructuredResultRow>();


        result.setTotal(docs.getNumFound());
        EfvTree<ColumnInfo> resultEfvs = new EfvTree<ColumnInfo>();
        EfoTree<ColumnInfo> resultEfos = qstate.getEfos();

        Iterable<EfvTree.EfEfv<ColumnInfo>> efvList = qstate.getEfvs().getValueSortedList();
        Iterable<EfoTree.EfoItem<ColumnInfo>> efoList = qstate.getEfos().getValueOrderedList();
        boolean hasQueryEfvs = qstate.hasQueryEfoEfvs();

        Maker<ColumnInfo> numberer = new Maker<ColumnInfo>() {
            private int num = 0;

            public ColumnInfo make() {
                return new BaseColumnInfo(num++);
            }
        };

        Collection<String> autoFactors = query.isFullHeatmap() ? efvService.getAllFactors() : efvService.getAnyConditionFactors();

        long overallBitStatsProcessingTime = 0;

        Set<Integer> geneIndexes = new HashSet<Integer>();
        for (SolrDocument doc : docs) {
            Long id = new Long((Integer) doc.getFieldValue("id"));
            if (id == null)
                continue;
            Integer geneIndex = atlasStatisticsQueryService.getIndexForGene(id);
            geneIndexes.add(geneIndex);
        }

        for (SolrDocument doc : docs) {
            Long id = new Long((Integer) doc.getFieldValue("id"));
            if (id == null)
                continue;
            GeneScore<Long> geneScore = new GeneScore<Long>(id);

            Map<Long, List<Long>> experiments = new HashMap<Long, List<Long>>();

            AtlasGene gene = new AtlasGene(doc);
            if (response.getHighlighting() != null)
                gene.setGeneHighlights(response.getHighlighting().get(id.toString()));

            List<UpdownCounter> counters = new ArrayList<UpdownCounter>() {
                @Override
                public UpdownCounter get(int index) {
                    if (index < size())
                        return super.get(index);
                    else
                        return new UpdownCounter(0, 0, 0, 0, 0);
                }
            };

            if (!hasQueryEfvs && query.getViewType() != ViewType.LIST) {
                int threshold = 0;

                if (!query.isFullHeatmap()) {
                    if (resultEfos.getNumExplicitEfos() > 0)
                        threshold = 1;
                    else if (resultEfos.getNumExplicitEfos() > 20)
                        threshold = 3;
                }

                for (ExpressionAnalysis ea : gene.getExpressionAnalyticsTable().getAll()) {
                    if (ea.isNo())
                        continue;

                    if (autoFactors.contains(ea.getEfName()))
                        resultEfvs.getOrCreate(ea.getEfName(), ea.getEfvName(), numberer);

                    if (!experiments.containsKey(ea.getEfvId()))
                        experiments.put(ea.getEfvId(), new ArrayList<Long>());

                    experiments.get(ea.getEfvId()).add(ea.getExperimentID());

                    for (String efo : ea.getEfoAccessions()) {
                        boolean isEfo = AtlasStatisticsQueryService.EFO_QUERY;
                        if (getExperimentCountsForGene(scoresCache, efo, StatisticsType.UP, isEfo, id, geneIndexes) > threshold) {
                            resultEfos.add(efo, numberer, false);
                        }
                    }
                }

                efvList = resultEfvs.getValueSortedList();
                efoList = resultEfos.getValueOrderedList();
            }

            Iterator<EfvTree.EfEfv<ColumnInfo>> itEfv = efvList.iterator();
            Iterator<EfoTree.EfoItem<ColumnInfo>> itEfo = efoList.iterator();
            EfvTree.EfEfv<ColumnInfo> efv = null;
            EfoTree.EfoItem<ColumnInfo> efo = null;
            while (itEfv.hasNext() || itEfo.hasNext() || efv != null || efo != null) {
                if (itEfv.hasNext() && efv == null)
                    efv = itEfv.next();
                if (itEfo.hasNext() && efo == null)
                    efo = itEfo.next();

                if (efv != null)
                    efv.setExperiments(experiments.get(efv.getEfEfvId()));

                UpdownCounter counter;
                boolean usingEfv = efo == null || (efv != null && efv.getPayload().compareTo(efo.getPayload()) < 0);
                String cellId;
                String efoOrEfv;
                boolean isEfo;
                if (usingEfv) {
                    cellId = efv.getEfEfvId();
                    efoOrEfv = cellId;
                    isEfo = !AtlasStatisticsQueryService.EFO_QUERY;
                } else {
                    efoOrEfv = efo.getId();
                    cellId = EscapeUtil.encode("efo", efoOrEfv);
                    isEfo = AtlasStatisticsQueryService.EFO_QUERY;
                }

                long timeStart = System.currentTimeMillis();
                int upCnt = getExperimentCountsForGene(scoresCache, efoOrEfv, StatisticsType.UP, isEfo, id, geneIndexes);
                int downCnt = getExperimentCountsForGene(scoresCache, efoOrEfv, StatisticsType.DOWN, isEfo, id, geneIndexes);
                int nonDECnt = getExperimentCountsForGene(scoresCache, efoOrEfv, StatisticsType.NON_D_E, isEfo, id, geneIndexes);
                long diff = System.currentTimeMillis() - timeStart;
                overallBitStatsProcessingTime += diff;
                if (diff > 50) {
                    log.debug("Retrieved attr: " + efoOrEfv + " counts for gene id: + " + id + " in: " + (System.currentTimeMillis() - timeStart) + " ms");
                }

                counter = new UpdownCounter(
                        upCnt,
                        downCnt,
                        nonDECnt,
                        EscapeUtil.nullzerof((Number) doc.getFieldValue("minpval_" + cellId + "_up")),
                        EscapeUtil.nullzerof((Number) doc.getFieldValue("minpval_" + cellId + "_dn")));


                counters.add(counter);


                boolean nonZero = (counter.getUps() + counter.getDowns() + counter.getNones() > 0);

                if (usingEfv) {
                    if (hasQueryEfvs && efv.getPayload().isQualified(counter)) {
                        resultEfvs.put(efv);
                        // Add counts for the purpose of ordering genes only if the counts for the current efv qualify against min experiments
                        geneScore.addCounts(counter.getUps(), counter.getDowns(), counter.getNones());
                    }
                    efv = null;
                } else {
                    if (nonZero) {
                        resultEfos.mark(efo.getId());
                        // Add counts for the purpose of ordering genes only if the counts for the current efo are non-zero
                        geneScore.addCounts(counter.getUps(), counter.getDowns(), counter.getNones());
                    } else {
                        log.debug("Rejecting " + efo.getId() + " for gene " + id + " as score 0" );
                    }
                    efo = null;
                }
            }

            if (query.getViewType() == ViewType.LIST) {
                loadListExperiments(result, gene, resultEfvs, resultEfos, qstate.getExperiments());
            }

            geneScoreToResultRow.put(geneScore, new StructuredResultRow(gene, counters));

        }


        // Returned results sorted by geneScore, eliminating that had zero qualifying score (i.e. all the scores added for all efvs where the counts were >= min experiments)
        for (GeneScore geneScore : geneScoreToResultRow.keySet()) {
            if (geneScore.isNonZero())
                result.addResult(geneScoreToResultRow.get(geneScore));
        }

        log.info("Overall bitstats processing time: " + overallBitStatsProcessingTime + " ms");

        result.setResultEfvs(resultEfvs);
        result.setResultEfos(resultEfos);

        log.info("Retrieved query completely: " + result.getSize() + " records of " +
                result.getTotal() + " total starting from " + result.getStart());

        log.debug("Resulting EFVs are: " + resultEfvs);
        log.debug("Resulting EFOs are: " + resultEfos);

    }

    /**
     * Loads experiments data for list view
     *
     * @param result      atlas result
     * @param efvTree     result efv tree
     * @param efoTree     result efo tree
     * @param experiments query experiments
     */
    private void loadListExperiments(AtlasStructuredQueryResult result, AtlasGene gene, final EfvTree<ColumnInfo> efvTree, final EfoTree<ColumnInfo> efoTree, Set<String> experiments) {
        Iterable<ExpressionAnalysis> exps = null;
        GeneExpressionAnalyticsTable table = gene.getExpressionAnalyticsTable();

        if (efvTree.getNumEfvs() + efoTree.getNumExplicitEfos() > 0) {
            Iterable<String> efviter = new Iterable<String>() {
                public Iterator<String> iterator() {
                    return new MappingIterator<EfvTree.EfEfv<ColumnInfo>, String>(efvTree.getNameSortedList().iterator()) {
                        public String map(EfvTree.EfEfv<ColumnInfo> efEfv) {
                            return efEfv.getEfEfvId();
                        }
                    };
                }
            };

            Iterable<String> efoiter = new Iterable<String>() {
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        private Iterator<String> explit = efoTree.getExplicitEfos().iterator();
                        private Iterator<String> childit;

                        public boolean hasNext() {
                            return explit.hasNext() || (childit != null && childit.hasNext());
                        }

                        public String next() {
                            if (childit != null) {
                                String r = childit.next();
                                if (!childit.hasNext() && explit.hasNext())
                                    childit = getEfo().getTermAndAllChildrenIds(explit.next()).iterator();
                                return r;
                            } else {
                                childit = getEfo().getTermAndAllChildrenIds(explit.next()).iterator();
                                return next();
                            }
                        }

                        public void remove() {
                        }
                    };
                }
            };
            exps = table.findByEfEfvEfoSet(efviter, efoiter);
        } else {
            exps = table.getAll();
        }

        Map<Pair<String, String>, List<ListResultRowExperiment>> map = new HashMap<Pair<String, String>, List<ListResultRowExperiment>>();
        for (ExpressionAnalysis exp : exps) {
            if (!experiments.isEmpty() && !experiments.contains(String.valueOf(exp.getExperimentID())))
                continue;
            AtlasExperiment aexp = atlasSolrDAO.getExperimentById(exp.getExperimentID());
            if (aexp != null) {
                Pair<String, String> key = new Pair<String, String>(exp.getEfName(), exp.getEfvName());
                if (!map.containsKey(key))
                    map.put(key, new ArrayList<ListResultRowExperiment>());
                map.get(key).add(new ListResultRowExperiment(exp.getExperimentID(),
                        aexp.getAccession(),
                        aexp.getDescription(),
                        exp.getPValAdjusted(),
                        exp.isNo() ? Expression.NONDE : (exp.isUp() ? Expression.UP : Expression.DOWN))
                );
            }
        }

        int listRowsPerGene = 0;
        for (Map.Entry<Pair<String, String>, List<ListResultRowExperiment>> e : map.entrySet()) {
            if (listRowsPerGene++ >= result.getRowsPerGene())
                break;

            int cup = 0, cdn = 0, cno = 0;
            float pup = 1, pdn = 1;
            long firstExperimentId = 0;
            for (ListResultRowExperiment exp : e.getValue()) {
                firstExperimentId = exp.getExperimentId();
                if (exp.getUpdn().isNo())
                    ++cno;
                else if (exp.getUpdn().isUp()) {
                    ++cup;
                    pup = Math.min(pup, exp.getPvalue());
                } else {
                    ++cdn;
                    pdn = Math.min(pdn, exp.getPvalue());
                }
            }

            ListResultRow row = new ListResultRow(e.getKey().getFirst(), e.getKey().getSecond(), cup, cdn, cno, pup, pdn, gene.getDesignElementId(firstExperimentId));
            row.setGene(gene);
            Collections.sort(e.getValue(), new Comparator<ListResultRowExperiment>() {
                public int compare(ListResultRowExperiment o1, ListResultRowExperiment o2) {
                    return Float.valueOf(o1.getPvalue()).compareTo(o2.getPvalue());
                }
            });
            row.setExp_list(e.getValue());
            result.addListResult(row);

        }
    }

    /**
     * Creates SOLR query from atlas query
     *
     * @param query  query
     * @param qstate query state
     * @return solr query object
     */
    private SolrQuery setupSolrQuery(AtlasStructuredQuery query, QueryState qstate) {
        SolrQuery q = new SolrQuery(qstate.getSolrq().toString());

        q.setRows(query.getRowsPerPage());
        q.setStart(query.getStart());
        q.setSortField("score", SolrQuery.ORDER.desc);

        q.setFacet(true);

        int max = 0;
        if (qstate.hasQueryEfoEfvs()) {
            for (EfvTree.Ef<ColumnInfo> ef : qstate.getEfvs().getNameSortedTree()) {
                if (max < ef.getEfvs().size())
                    max = ef.getEfvs().size();

                for (EfvTree.Efv<ColumnInfo> efv : ef.getEfvs()) {
                    q.addField("minpval_" + EscapeUtil.encode(ef.getEf(), efv.getEfv()) + "_up");
                    q.addField("minpval_" + EscapeUtil.encode(ef.getEf(), efv.getEfv()) + "_dn");
                }
            }

            if (max < qstate.getEfos().getNumEfos()) {
                max = qstate.getEfos().getNumEfos();
            }
            for (String id : qstate.getEfos().getEfoIds()) {
                String ide = EscapeUtil.encode(id);
                q.addField("minpval_efo_" + ide + "_up");
                q.addField("minpval_efo_" + ide + "_dn");
            }

            q.addField("score");
            q.addField("id");
            q.addField("name");
            q.addField("identifier");
            q.addField("species");
            q.addField("exp_info");
            for (String p : genePropService.getIdNameDescProperties())
                q.addField("property_" + p);
        } else {
            q.addField("*");
        }
        q.setFacetLimit(5 + max);

        q.setFacetMinCount(2);
        q.setFacetSort(FacetParams.FACET_SORT_COUNT);

        for (String p : genePropService.getDrilldownProperties()) {
            q.addFacetField("property_f_" + p);
        }

        q.addFacetField("species");
        q.addFacetField("exp_up_ids");
        q.addFacetField("exp_dn_ids");

        for (String ef : efvService.getFacetFactors()) {
            q.addFacetField("efvs_up_" + ef);
            q.addFacetField("efvs_dn_" + ef);
        }

        q.setHighlight(true);
        q.setHighlightSnippets(100);
        q.setParam("hl.usePhraseHighlighter", "true");
        q.setParam("hl.mergeContiguous", "true");
        q.setHighlightRequireFieldMatch(true);
        q.addHighlightField("id");
        q.addHighlightField("name");
        q.addHighlightField("synonym");
        q.addHighlightField("identifier");
        for (String p : genePropService.getIdNameDescProperties())
            q.addHighlightField("property_" + p);
        return q;
    }

    /**
     * Retrieves gene facets from SOLR response
     *
     * @param response solr response
     * @param name     field name to exptract
     * @param values   query values (to clear off the facet)
     * @return iterable collection of facet values with counters
     */
    private Iterable<FacetCounter> getGeneFacet(QueryResponse response, final String name, Set<String> values) {
        List<FacetCounter> facet = new ArrayList<FacetCounter>();
        FacetField ff = response.getFacetField(name);
        if (ff == null || ff.getValueCount() < 2 || ff.getValues() == null)
            return new ArrayList<FacetCounter>();

        for (FacetField.Count ffc : ff.getValues())
            if (!values.contains(ffc.getName()))
                facet.add(new FacetCounter(ffc.getName(), (int) ffc.getCount()));
        if (facet.size() < 2)
            return new ArrayList<FacetCounter>();

        Collections.sort(facet);
        return facet.subList(0, Math.min(facet.size(), 5));

    }

    /**
     * Retrieves EFVs facets tree
     *
     * @param response solr response
     * @param qstate   query state
     * @return efvtree of facet counters
     */
    private EfvTree<FacetUpDn> getEfvFacet(QueryResponse response, QueryState qstate) {
        EfvTree<FacetUpDn> efvFacet = new EfvTree<FacetUpDn>();
        Maker<FacetUpDn> creator = new Maker<FacetUpDn>() {
            public FacetUpDn make() {
                return new FacetUpDn();
            }
        };
        for (FacetField ff : response.getFacetFields()) {
            if (ff.getValueCount() > 1) {
                if (ff.getName().startsWith("efvs_")) {
                    String ef = ff.getName().substring(8);
                    for (FacetField.Count ffc : ff.getValues()) {
                        if (!qstate.efvs.has(ef, ffc.getName())) {
                            int count = (int) ffc.getCount();
                            efvFacet.getOrCreate(ef, ffc.getName(), creator)
                                    .add(count, ff.getName().substring(5, 7).equals("up"));
                        }
                    }
                } else if (ff.getName().startsWith("exp_")) {
                    for (FacetField.Count ffc : ff.getValues())
                        if (!qstate.getExperiments().contains(ffc.getName())) {
                            AtlasExperiment exp = atlasSolrDAO.getExperimentById(ffc.getName());
                            if (exp != null) {
                                String expName = exp.getAccession();
                                if (expName != null) {
                                    int count = (int) ffc.getCount();
                                    efvFacet.getOrCreate(Constants.EXP_FACTOR_NAME, expName, creator)
                                            .add(count, ff.getName().substring(4, 6).equals("up"));
                                }
                            }
                        }
                }
            }
        }
        return efvFacet;
    }

    /**
     * Returns set of experimental factor for drop-down, fileterd by config
     *
     * @return set of strings representing experimental factors
     */
    public Collection<String> getExperimentalFactorOptions() {
        List<String> factors = new ArrayList<String>();
        factors.addAll(efvService.getOptionsFactors());
        factors.add(Constants.EXP_FACTOR_NAME);
        Collections.sort(factors, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return atlasProperties.getCuratedEf(o1).compareToIgnoreCase(atlasProperties.getCuratedGeneProperty(o2));
            }
        });
        return factors;
    }

    /**
     * Returns list of available gene property options sorted by curated value
     *
     * @return list of strings
     */
    public List<String> getGenePropertyOptions() {
        List<String> result = new ArrayList<String>();
        for (String v : genePropService.getIdNameDescProperties())
            result.add(v);
        result.add(Constants.GENE_PROPERTY_NAME);
        Collections.sort(result, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return atlasProperties.getCuratedGeneProperty(o1).compareToIgnoreCase(atlasProperties.getCuratedGeneProperty(o2));
            }
        });
        return result;
    }

    /**
     * Returns list of available species
     *
     * @return list of species strings
     */
    public SortedSet<String> getSpeciesOptions() {
        if (allSpecies.isEmpty()) {
            SolrQuery q = new SolrQuery("*:*");
            q.setRows(0);
            q.addFacetField("species");
            q.setFacet(true);
            q.setFacetLimit(-1);
            q.setFacetMinCount(1);
            q.setFacetSort(FacetParams.FACET_SORT_COUNT);
            try {
                QueryResponse qr = solrServerAtlas.query(q);
                if (qr.getFacetFields().get(0).getValues() != null) {
                    for (FacetField.Count ffc : qr.getFacetFields().get(0).getValues()) {
                        allSpecies.add(ffc.getName());
                    }
                }
            } catch (SolrServerException e) {
                throw new RuntimeException("Can't fetch all factors", e);
            }
        }
        return allSpecies;
    }

    /**
     * Index rebuild notification handler
     *
     * @param builder builder
     * @param event   event
     */
    public void onIndexBuildFinish(IndexBuilder builder, IndexBuilderEvent event) {
        allSpecies.clear();
    }

    public void onIndexBuildStart(IndexBuilder builder) {

    }

    /**
     * Destructor called by Spring
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        if (indexBuilder != null)
            indexBuilder.unregisterIndexBuildEventHandler(this);
    }
}
