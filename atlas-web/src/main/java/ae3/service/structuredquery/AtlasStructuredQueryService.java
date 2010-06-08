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
import uk.ac.ebi.gxa.utils.*;
import uk.ac.ebi.gxa.index.builder.IndexBuilderEventHandler;
import uk.ac.ebi.gxa.index.builder.IndexBuilder;
import uk.ac.ebi.gxa.index.builder.listener.IndexBuilderEvent;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.microarray.atlas.model.ExpressionAnalysis;

import java.util.*;

/**
 * Structured query support class
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

    private AtlasSolrDAO atlasSolrDAO;

    private CoreContainer coreContainer;

    private Efo efo;

    private final Set<String> cacheFill = new HashSet<String>();
    private SortedSet<String> allSpecies = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Hack: prevents OOMs by clearing cache
     */
    private void controlCache() {
        if(coreContainer == null)
            return;

        synchronized (cacheFill) {
            if(cacheFill.size() > 500) {
                SolrCore core = coreContainer.getCore(Constants.CORE_ATLAS);
                if( core != null ) {
                    core.closeSearcher();
                    core.close();
                }
                cacheFill.clear();
            }
        }
    }

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

    private static class SolrQueryBuilder {
        private StringBuffer solrq = new StringBuffer();
        private StringBuffer scores = new StringBuffer();

        private static final EnumMap<QueryExpression,String> SCORE_EXP_MAP = new EnumMap<QueryExpression,String>(QueryExpression.class);

        static {
            SCORE_EXP_MAP.put(QueryExpression.UP, "_up");
            SCORE_EXP_MAP.put(QueryExpression.DOWN, "_dn");
            SCORE_EXP_MAP.put(QueryExpression.UP_ONLY, "_up");
            SCORE_EXP_MAP.put(QueryExpression.DOWN_ONLY, "_dn");
            SCORE_EXP_MAP.put(QueryExpression.UP_DOWN, "_ud");
            SCORE_EXP_MAP.put(QueryExpression.NON_D_E, "_no");
        }

        public SolrQueryBuilder appendAnd() {
            if(solrq.length() > 0)
                solrq.append(" AND ");
            return this;
        }

        public SolrQueryBuilder append(String s) {
            solrq.append(s);
            return this;
        }

        public SolrQueryBuilder append(Object s) {
            solrq.append(s);
            return this;
        }

        public SolrQueryBuilder append(StringBuffer s) {
            solrq.append(s);
            return this;
        }

        public SolrQueryBuilder appendScore(String s) {
            if(scores.length() > 0)
                scores.append(",");
            scores.append(s);
            return this;
        }

        public SolrQueryBuilder appendExpFields(String prefix, String id, QueryExpression e, int minExp) {
            String minExpS = minExp == 1 ? "*" : String.valueOf(minExp);
            switch(e)
            {
                case UP_ONLY:
                case UP:
                    solrq.append(prefix).append(id).append("_up:[").append(minExpS).append(" TO *]"); break;
                case DOWN_ONLY:
                case DOWN:
                    solrq.append(prefix).append(id).append("_dn:[").append(minExpS).append(" TO *]"); break;
                case UP_DOWN: solrq.append(prefix).append(id).append("_up:[").append(minExpS).append(" TO *] ")
                        .append(prefix).append(id).append("_dn:[").append(minExpS).append(" TO *]"); break;
                case NON_D_E:
                    solrq.append(prefix).append(id).append("_no:[").append(minExpS).append(" TO *] "); break;
                default:
                    throw new IllegalArgumentException("Unknown regulation option specified " + e);
            }
            return this;
        }

        public SolrQueryBuilder appendExpScores(String prefix, String id, QueryExpression e) {
            if(scores.length() > 0)
                scores.append(",");
            scores.append(prefix).append(id).append(SCORE_EXP_MAP.get(e));
            return this;
        }


        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer(solrq);
            if(scores.length() > 0)
                sb.append(" AND _val_:\"sum(").append(scores).append(")\"");
            else
                sb.append(" AND _val_:\"sum(cnt_efo_EFO_0000001_up,cnt_efo_EFO_0000001_dn)\"");
            return sb.toString();
        }

        public boolean isEmpty() {
            return solrq.length() == 0;
        }
    }

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

    private static class QueryColumnInfo extends BaseColumnInfo {
        private int minUpExperiments = Integer.MAX_VALUE;
        private int minDnExperiments = Integer.MAX_VALUE;
        private int minOrExperiments = Integer.MAX_VALUE;
        private int minNoExperiments = Integer.MAX_VALUE;

        private QueryColumnInfo(int position) {
            super(position);
        }

        public void update(QueryExpression expression, int minExperiments) {
            switch(expression) {
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

        public boolean isQualified(UpdownCounter ud) {
            if(ud.getUps() >= minUpExperiments)
                return true;
            if(ud.getDowns() >= minDnExperiments)
                return true;
            if(ud.getNones() >= minNoExperiments)
                return true;
            if(ud.getUps() >= minOrExperiments || ud.getDowns() >= minOrExperiments)
                return true;
            return false;
        }
    }

    private class QueryState {
        private final SolrQueryBuilder solrq = new SolrQueryBuilder();
        private final EfvTree<ColumnInfo> efvs = new EfvTree<ColumnInfo>();
        private final EfoTree<ColumnInfo> efos = new EfoTree<ColumnInfo>(getEfo());
        private final Set<String> experiments = new HashSet<String>();

        private int num = 0;

        private Maker<ColumnInfo> numberer = new Maker<ColumnInfo>() {
            public ColumnInfo make() { return new QueryColumnInfo(num++); }
        };

        public SolrQueryBuilder getSolrq() {
            return solrq;
        }

        public void addExperiments(Collection<String> ids) {
            experiments.addAll(ids);
        }

        public void addEfv(String ef, String efv, int minExperiments, QueryExpression expression) {
            ((QueryColumnInfo)efvs.getOrCreate(ef, efv, numberer)).update(expression, minExperiments);
        }

        public void addEfo(String id, int minExperiments, QueryExpression expression) {
            for(ColumnInfo ci : efos.add(id, numberer, true))
                ((QueryColumnInfo)ci).update(expression, minExperiments);
        }

        public Set<String> getExperiments() {
            return experiments;
        }

        public EfvTree<ColumnInfo> getEfvs() {
            return efvs;
        }

        public EfoTree<ColumnInfo> getEfos() {
            return efos;
        }

        public boolean isEmpty() {
            return solrq.isEmpty();
        }

        public boolean hasQueryEfoEfvs() {
            return efvs.getNumEfvs() + efos.getNumEfos() > 0;
        }

        @Override
        public String toString() {
            return "SOLR query: <" + solrq.toString() + ">, Experiments: [" + StringUtils.join(experiments, ", ") + "], "
                    + "EFVs: [" + efvs.toString() + "], EFOs: [" + efos.toString() + "]";
        }
    }


    /**
     * Process structured Atlas query
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

        AtlasStructuredQueryResult result = new AtlasStructuredQueryResult(query.getStart(), query.getRowsPerPage(), query.getExpsPerGene());
        result.setConditions(conditions);

        if(!qstate.isEmpty())
        {
            try {

                controlCache();

                SolrQuery q = setupSolrQuery(query, qstate);
                QueryResponse response = solrServerAtlas.query(q);

                processResultGenes(response, result, qstate, query);

                Set<String> expandableEfs = new HashSet<String>();
                EfvTree<ColumnInfo> trimmedEfvs = trimColumns(query, result, expandableEfs);
                result.setResultEfvs(trimmedEfvs);
                result.setExpandableEfs(expandableEfs);

                if(response.getFacetFields() != null) {
                    result.setEfvFacet(getEfvFacet(response, qstate));
                    for(String p : genePropService.getDrilldownProperties()) {
                        Set<String> hasVals = new HashSet<String>();
                        for(GeneQueryCondition qc : query.getGeneConditions())
                            if(qc.getFactor().equals(p))
                                hasVals.addAll(qc.getFactorValues());

                        Iterable<FacetCounter> facet = getGeneFacet(response, "property_f_" + p, hasVals);
                        if(facet.iterator().hasNext())
                            result.setGeneFacet(p, facet);
                    }
                    if(!query.getSpecies().iterator().hasNext())
                        result.setGeneFacet("species", getGeneFacet(response, "species", new HashSet<String>()));
                }
            } catch (SolrServerException e) {
                log.error("Error in structured query!", e);
            }
        }

        return result;
    }

    public List<ListResultRow> findGenesForExperiment(Object geneIds, long experimentId, int start, int rows) {
        AtlasStructuredQueryResult result = doStructuredAtlasQuery(new AtlasStructuredQueryBuilder()
                .andGene(geneIds)
                .andUpdnIn(Constants.EXP_FACTOR_NAME, String.valueOf(experimentId))
                .viewAs(ViewType.LIST)
                .rowsPerPage(rows)
                .startFrom(start)
                .expsPerGene(atlasProperties.getQueryExperimentsPerGene()).query());

        List<ListResultRow> res = new ArrayList<ListResultRow>();
        for(AtlasStructuredQueryResult.ListResultGene gene : result.getListResultsGenes()) {
            ListResultRow minRow = null;
            float minPvalue = 1;
            for(ListResultRow row : gene.getExpressions()) {
                float pvalue = 1;
                for(ListResultRowExperiment e : row.getExp_list()) {
                    if(e.getExperimentId() == experimentId) {
                        pvalue = e.getPvalue();
                        row.setExp_list(Collections.singleton(e));
                        break;
                    }
                }
                if(minRow == null || pvalue < minPvalue) {
                    minRow = row;
                    minPvalue = pvalue;
                }
            }
            if(minRow != null)
                res.add(minRow);
        }
        return res;
    }

    private EfvTree<ColumnInfo> trimColumns(final AtlasStructuredQuery query,
                                         final AtlasStructuredQueryResult result,
                                         Collection<String> expandableEfs)
    {
        final Set<String> expand = query.getExpandColumns();
        EfvTree<ColumnInfo> trimmedEfvs = new EfvTree<ColumnInfo>(result.getResultEfvs());
        if(expand.contains("*"))
            return trimmedEfvs;

        if(trimmedEfvs.getNumEfvs() < MAX_EFV_COLUMNS)
            return trimmedEfvs;


        int threshold = MAX_EFV_COLUMNS / trimmedEfvs.getNumEfs();

        for(EfvTree.Ef<ColumnInfo> ef : trimmedEfvs.getNameSortedTree())
        {
            if(expand.contains(ef.getEf()) || ef.getEfvs().size() < threshold)
                continue;

            Map<EfvTree.Efv<ColumnInfo>,Double> scores = new HashMap<EfvTree.Efv<ColumnInfo>,Double>();
            for(EfvTree.Efv<ColumnInfo> efv : ef.getEfvs())
                scores.put(efv, 0.0);

            for(StructuredResultRow row : result.getResults())
            {
                for(EfvTree.Efv<ColumnInfo> efv : ef.getEfvs())
                {
                    UpdownCounter c = row.getCounters().get(efv.getPayload().getPosition());
                    scores.put(efv, scores.get(efv) + c.getDowns() * (1.0 - c.getMpvDn()) + c.getUps() * (1.0 - c.getMpvUp()));
                }
            }

            @SuppressWarnings("unchecked")
            Map.Entry<EfvTree.Efv<ColumnInfo>,Double>[] scoreset = scores.entrySet().toArray(new Map.Entry[1]);
            Arrays.sort(scoreset, new Comparator<Map.Entry<EfvTree.Efv<ColumnInfo>,Double>>() {
                public int compare(Map.Entry<EfvTree.Efv<ColumnInfo>, Double> o1, Map.Entry<EfvTree.Efv<ColumnInfo>, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            for(int i = threshold; i < scoreset.length; ++i)
            {
                trimmedEfvs.removeEfv(ef.getEf(), scoreset[i].getKey().getEfv());
                expandableEfs.add(ef.getEf());
            }
        }

        return trimmedEfvs;
    }

    private Collection<String> findExperiments(String query, EfvTree<Boolean> condEfvs) throws SolrServerException {

        List<String> result = new ArrayList<String>();
        if(query.length() == 0)
            return result;

        SolrQuery q = new SolrQuery("id:(" + query + ") accession:(" + query + ")");
        q.addField("*");
        q.setRows(50);
        q.setStart(0);

        QueryResponse qr = solrServerExpt.query(q);
        for(SolrDocument doc : qr.getResults()) {
            String id = String.valueOf(doc.getFieldValue("id"));
            if(id != null) {
                result.add(id);
                for(String name : doc.getFieldNames())
                    if(name.startsWith("a_property_"))
                        for(Object val : doc.getFieldValues(name))
                            condEfvs.put(name.substring("a_property_".length()), String.valueOf(val), true);
            }
        }

        return result;
    }

    private StringBuffer makeExperimentsQuery(Iterable<String> ids, QueryExpression e) {
        StringBuffer sb = new StringBuffer();
        String idss = StringUtils.join(ids.iterator(), " ");
        if(idss.length() == 0)
            return sb;
        switch(e) {
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

    private Iterable<ExpFactorResultCondition> appendEfvsQuery(final AtlasStructuredQuery query, final QueryState qstate) {
        final List<ExpFactorResultCondition> conds = new ArrayList<ExpFactorResultCondition>();
        SolrQueryBuilder solrq = qstate.getSolrq();

        for(ExpFactorQueryCondition c : query.getConditions())
        {
            boolean isExperiment = Constants.EXP_FACTOR_NAME.equals(c.getFactor());
            if(c.isAnything() || (isExperiment && c.isAnyValue())) {
                // do nothing
            } else if(c.isOnly() && !c.isAnyFactor()
                    && !Constants.EFO_FACTOR_NAME.equals(c.getFactor())
                    && !Constants.EXP_FACTOR_NAME.equals(c.getFactor())) {
                try {
                    EfvTree<Boolean> condEfvs = getCondEfvsForFactor(c.getFactor(), c.getFactorValues());
                    EfvTree<Boolean> allEfvs = getCondEfvsAllForFactor(c.getFactor());
                    if(condEfvs.getNumEfs() + allEfvs.getNumEfs() > 0) {
                        solrq.appendAnd().append("((");
                        for(EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList())
                        {
                            solrq.append(" ");

                            String efefvId = condEfv.getEfEfvId();
                            solrq.appendExpFields("cnt_", efefvId, c.getExpression(), c.getMinExperiments());
                            solrq.appendExpScores("s_", efefvId, c.getExpression());

                            notifyCache(efefvId + c.getExpression());
                            qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                        }
                        solrq.append(")");
                        for(EfvTree.EfEfv<Boolean> allEfv : allEfvs.getNameSortedList())
                            if(!condEfvs.has(allEfv.getEf(), allEfv.getEfv()))
                            {
                                String efefvId = allEfv.getEfEfvId();
                                solrq.append(" AND NOT (");
                                solrq.appendExpFields("cnt_", efefvId, c.getExpression(), 1);
                                solrq.append(")");
                                notifyCache(efefvId + c.getExpression());
                                qstate.addEfv(allEfv.getEf(), allEfv.getEfv(), 1, QueryExpression.UP_DOWN);
                            }
                        solrq.append(")");
                        conds.add(new ExpFactorResultCondition(c,
                                Collections.<List<AtlasEfoService.EfoTermCount>>emptyList(),
                                false));
                    }
                }  catch (SolrServerException e) {
                    log.error("Error querying Atlas index", e);
                }

            } else {
                try {
                    boolean nonemptyQuery = false;
                    EfvTree<Boolean> condEfvs = isExperiment ? new EfvTree<Boolean>() : getConditionEfvs(c);
                    if(condEfvs.getNumEfs() > 0)
                    {
                        solrq.appendAnd().append("(");
                        int i = 0;
                        for(EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList())
                        {
                            if(++i > 100)
                                break;

                            solrq.append(" ");

                            String efefvId = condEfv.getEfEfvId();
                            solrq.appendExpFields("cnt_", efefvId, c.getExpression(), c.getMinExperiments());
                            solrq.appendExpScores("s_", efefvId, c.getExpression());

                            notifyCache(efefvId + c.getExpression());

                            if(Constants.EFO_FACTOR_NAME.equals(condEfv.getEf())) {
                                qstate.addEfo(condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                            } else {
                                qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                            }
                        }
                        solrq.append(")");
                        nonemptyQuery = true;
                    } else if(c.isAnyFactor() || isExperiment) {
                        // try to search for experiment too if no matching conditions are found
                        Collection<String> experiments = findExperiments(c.getSolrEscapedFactorValues(), condEfvs);
                        qstate.addExperiments(experiments);
                        StringBuffer expq = makeExperimentsQuery(experiments, c.getExpression());
                        if(expq.length() > 0) {
                            for(EfvTree.EfEfv<Boolean> condEfv : condEfvs.getNameSortedList())
                            {
                                qstate.addEfv(condEfv.getEf(), condEfv.getEfv(), c.getMinExperiments(), c.getExpression());
                                solrq.appendExpScores("s_efv_", condEfv.getEfEfvId(), c.getExpression());
                            }
                            solrq.appendAnd().append(expq);
                            nonemptyQuery = true;
                        }
                    }
                    Collection<List<AtlasEfoService.EfoTermCount>> efoPaths = new ArrayList<List<AtlasEfoService.EfoTermCount>>();
                    Collection<EfvTree.Efv<Boolean>> condEfos = condEfvs.getEfvs(Constants.EFO_FACTOR_NAME);
                    for(EfvTree.Efv<Boolean> efv : condEfos) {
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

    private void appendGeneQuery(AtlasStructuredQuery query, SolrQueryBuilder solrq) {
    	for(GeneQueryCondition geneQuery : query.getGeneConditions()) {
            solrq.appendAnd();
            if(geneQuery.isNegated())
                solrq.append(" NOT ");

            String escapedQ = geneQuery.getSolrEscapedFactorValues();
            if(geneQuery.isAnyFactor()) {
                solrq.append("(name:(").append(escapedQ).append(") species:(").append(escapedQ)
                    .append(") identifier:(").append(escapedQ).append(") id:(").append(escapedQ).append(")");
                for(String p : genePropService.getIdNameDescProperties())
                    solrq.append(" property_").append(p).append(":(").append(escapedQ).append(")");
                solrq.append(") ");
            } else if(Constants.GENE_PROPERTY_NAME.equals(geneQuery.getFactor())) {
                solrq.append("(name:(").append(escapedQ).append(") ");
                solrq.append("identifier:(").append(escapedQ).append(") ");
                solrq.append("id:(").append(escapedQ).append(") ");
                for(String nameProp : genePropService.getNameProperties())
                    solrq.append("property_" + nameProp + ":(").append(escapedQ).append(") ");
                solrq.append(")");
            } else if(genePropService.getDescProperties().contains(geneQuery.getFactor())
                    || genePropService.getIdProperties().contains(geneQuery.getFactor())) {
                String field = "property_" + geneQuery.getFactor();
                solrq.append(field).append(":(").append(escapedQ).append(")");
            }
    	}
    }

    private void appendSpeciesQuery(AtlasStructuredQuery query, SolrQueryBuilder solrq) {
        Set<String> species = new HashSet<String>();
        for(String s : query.getSpecies())
            for(String as : getSpeciesOptions())
                if(as.toLowerCase().contains(s.toLowerCase()))
                    species.add(as);

        if(!species.isEmpty()) {
            solrq.appendAnd().append("species:(").append(EscapeUtil.escapeSolrValueList(species)).append(")");
        }
    }

    private EfvTree<Boolean> getConditionEfvs(QueryCondition c) throws SolrServerException {
        if(c.isAnyValue())
            return getCondEfvsAllForFactor(c.getFactor());

        if(c.isAnyFactor())
            return getCondEfvsForFactor(null, c.getFactorValues());

        return getCondEfvsForFactor(c.getFactor(), c.getFactorValues());
    }

    private EfvTree<Boolean> getCondEfvsAllForFactor(String factor) {
        EfvTree<Boolean> condEfvs = new EfvTree<Boolean>();
        if(Constants.EFO_FACTOR_NAME.equals(factor)) {
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

    private EfvTree<Boolean> getCondEfvsForFactor(final String factor, final Iterable<String> values) throws SolrServerException {

        EfvTree<Boolean> condEfvs = new EfvTree<Boolean>();

        if(Constants.EFO_FACTOR_NAME.equals(factor) || null == factor) {
            Efo efo = getEfo();
            for(String v : values) {
                for(EfoTerm term : efo.searchTerm(EscapeUtil.escapeSolr(v))) {
                    condEfvs.put(Constants.EFO_FACTOR_NAME, term.getId(), true);
                }
            }
        }

        if(Constants.EFO_FACTOR_NAME.equals(factor))
            return condEfvs;

        String queryString = EscapeUtil.escapeSolrValueList(values);
        if(factor != null)
            queryString = "(" + queryString + ") AND property:" + EscapeUtil.escapeSolr(factor);

        SolrQuery q = new SolrQuery(queryString);
        q.setRows(10000);
        q.setStart(0);
        q.setFields("*");

        QueryResponse qr = solrServerProp.query(q);

        for(SolrDocument doc : qr.getResults())
        {
            String ef = (String)doc.getFieldValue("property");
            String efv = (String)doc.getFieldValue("value");
            condEfvs.put(ef, efv, true);
        }
        return condEfvs;
    }


    private void processResultGenes(QueryResponse response,
                                    AtlasStructuredQueryResult result,
                                    QueryState qstate, AtlasStructuredQuery query) throws SolrServerException {

        SolrDocumentList docs = response.getResults();
        result.setTotal(docs.getNumFound());
        EfvTree<ColumnInfo> resultEfvs = new EfvTree<ColumnInfo>();
        EfoTree<ColumnInfo> resultEfos = qstate.getEfos();

        Iterable<EfvTree.EfEfv<ColumnInfo>> efvList = qstate.getEfvs().getValueSortedList();
        Iterable<EfoTree.EfoItem<ColumnInfo>> efoList = qstate.getEfos().getValueOrderedList();
        boolean hasQueryEfvs = qstate.hasQueryEfoEfvs();

        Maker<ColumnInfo> numberer = new Maker<ColumnInfo>() {
            private int num = 0;
            public ColumnInfo make() { return new BaseColumnInfo(num++); }
        };

        Collection<String> autoFactors = query.isFullHeatmap() ? efvService.getAllFactors() : efvService.getAnyConditionFactors();

        for(SolrDocument doc : docs) {
            Integer id = (Integer)doc.getFieldValue("id");
            if(id == null)
                continue;

            AtlasGene gene = new AtlasGene(doc);
            if(response.getHighlighting() != null)
                gene.setGeneHighlights(response.getHighlighting().get(id.toString()));

            List<UpdownCounter> counters = new ArrayList<UpdownCounter>() {
                @Override
                public UpdownCounter get(int index) {
                    if(index < size())
                        return super.get(index);
                    else
                        return new UpdownCounter(0, 0, 0, 0, 0);
                }
            };

            if(!hasQueryEfvs && query.getViewType() != ViewType.LIST) {
                int threshold = 0;

                if(!query.isFullHeatmap()) {
                    if(resultEfos.getNumExplicitEfos() > 0)
                        threshold = 1;
                    else if(resultEfos.getNumExplicitEfos() > 20)
                        threshold = 3;
                }

                for(ExpressionAnalysis ea : gene.getExpressionAnalyticsTable().getAll()) {
                    if(ea.isNo())
                        continue;

                    if(autoFactors.contains(ea.getEfName()))
                        resultEfvs.getOrCreate(ea.getEfName(), ea.getEfvName(), numberer);

                    for(String efo : ea.getEfoAccessions())
                        if(EscapeUtil.nullzero((Number)doc.getFieldValue("cnt_efo_" + EscapeUtil.encode(efo) + "_s_up")) > threshold)
                            resultEfos.add(efo, numberer, false);
                }

                efvList = resultEfvs.getValueSortedList();
                efoList = resultEfos.getValueOrderedList();
            }

            Iterator<EfvTree.EfEfv<ColumnInfo>> itEfv = efvList.iterator();
            Iterator<EfoTree.EfoItem<ColumnInfo>> itEfo = efoList.iterator();
            EfvTree.EfEfv<ColumnInfo> efv = null;
            EfoTree.EfoItem<ColumnInfo> efo = null;
            while(itEfv.hasNext() || itEfo.hasNext() || efv != null || efo != null)
            {
                if(itEfv.hasNext() && efv == null)
                    efv = itEfv.next();
                if(itEfo.hasNext() && efo == null)
                    efo = itEfo.next();

                String cellId;
                boolean usingEfv = efo == null || (efv != null && efv.getPayload().compareTo(efo.getPayload()) < 0);
                if(usingEfv) {
                    cellId = efv.getEfEfvId();
                } else {
                    cellId = EscapeUtil.encode("efo", efo.getId());
                }

                UpdownCounter counter = new UpdownCounter(
                        EscapeUtil.nullzero((Number)doc.getFieldValue("cnt_" + cellId + "_up")),
                        EscapeUtil.nullzero((Number)doc.getFieldValue("cnt_" + cellId + "_dn")),
                        EscapeUtil.nullzero((Number)doc.getFieldValue("cnt_" + cellId + "_no")),
                        EscapeUtil.nullzerof((Number)doc.getFieldValue("minpval_" + cellId + "_up")),
                        EscapeUtil.nullzerof((Number)doc.getFieldValue("minpval_" + cellId + "_dn")));

                counters.add(counter);

                boolean nonZero = (counter.getUps() + counter.getDowns() + counter.getNones() > 0);

                if (usingEfv) {
                    if (hasQueryEfvs && efv.getPayload().isQualified(counter))
                        resultEfvs.put(efv);
                    efv = null;
                } else {
                    if (nonZero)
                        resultEfos.mark(efo.getId());
                    efo = null;
                }
            }

            if(query.getViewType() == ViewType.LIST) {
                loadListExperiments(result, gene, resultEfvs, resultEfos, qstate.getExperiments());
            }

            result.addResult(new StructuredResultRow(gene, counters));
        }

        result.setResultEfvs(resultEfvs);
        result.setResultEfos(resultEfos);

        log.info("Retrieved query completely: " + result.getSize() + " records of " +
                result.getTotal() + " total starting from " + result.getStart() );

        log.debug("Resulting EFVs are: " + resultEfvs);
        log.debug("Resulting EFOs are: " + resultEfos);

    }

    /**
     * TODO
     * @param result
     * @param efvTree
     * @param efoTree
     * @param experiments
     */
    private void loadListExperiments(AtlasStructuredQueryResult result, AtlasGene gene, final EfvTree<ColumnInfo> efvTree, final EfoTree<ColumnInfo> efoTree, Set<String> experiments) {
        Iterable<ExpressionAnalysis> exps = null;
        GeneExpressionAnalyticsTable table = gene.getExpressionAnalyticsTable();

        if(efvTree.getNumEfvs() + efoTree.getNumExplicitEfos() > 0) {
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
                            if(childit != null) {
                                String r = childit.next();
                                if(!childit.hasNext() && explit.hasNext())
                                    childit = getEfo().getTermAndAllChildrenIds(explit.next()).iterator();
                                return r;
                            } else {
                                childit = getEfo().getTermAndAllChildrenIds(explit.next()).iterator();
                                return next();
                            }
                        }
                        public void remove() { }
                    };
                }
            };
            exps = table.findByEfEfvEfoSet(efviter, efoiter);
        } else {
            exps = table.getAll();
        }

        Map<Pair<String,String>,List<ListResultRowExperiment>> map = new HashMap<Pair<String,String>, List<ListResultRowExperiment>>();
        for(ExpressionAnalysis exp : exps) {
        	if(!experiments.isEmpty() && !experiments.contains(String.valueOf(exp.getExperimentID())))
        		continue;
            AtlasExperiment aexp = atlasSolrDAO.getExperimentById(exp.getExperimentID());
            if(aexp != null) {
                Pair<String,String> key = new Pair<String,String>(exp.getEfName(), exp.getEfvName());
                if(!map.containsKey(key))
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
        for(Map.Entry<Pair<String,String>,List<ListResultRowExperiment>> e : map.entrySet()) {
            if(listRowsPerGene++ >= result.getRowsPerGene())
                break;

            int cup = 0, cdn = 0, cno = 0;
            float pup = 1, pdn = 1;
            for(ListResultRowExperiment exp : e.getValue())
                if(exp.getUpdn().isNo())
                    ++cno;
                else if(exp.getUpdn().isUp()) {
                    ++cup;
                    pup = Math.min(pup, exp.getPvalue());
                } else {
                    ++cdn;
                    pdn = Math.min(pdn, exp.getPvalue());
                }

            ListResultRow row = new ListResultRow(e.getKey().getFirst(), e.getKey().getSecond(), cup, cdn, cno, pup, pdn);
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


    private SolrQuery setupSolrQuery(AtlasStructuredQuery query, QueryState qstate) {
        SolrQuery q = new SolrQuery(qstate.getSolrq().toString());

        q.setRows(query.getRowsPerPage());
        q.setStart(query.getStart());
        q.setSortField("score", SolrQuery.ORDER.desc);

        q.setFacet(true);

        int max = 0;
        if(qstate.hasQueryEfoEfvs())
        {
            for(EfvTree.Ef<ColumnInfo> ef : qstate.getEfvs().getNameSortedTree())
            {
                if(max < ef.getEfvs().size())
                    max = ef.getEfvs().size();

                for(EfvTree.Efv<ColumnInfo> efv : ef.getEfvs()) {
                    q.addField("cnt_" + EfvTree.getEfEfvId(ef, efv) + "_up");
                    q.addField("cnt_" + EfvTree.getEfEfvId(ef, efv) + "_dn");
                    q.addField("cnt_" + EfvTree.getEfEfvId(ef, efv) + "_no");
                    q.addField("minpval_" + EfvTree.getEfEfvId(ef, efv) + "_up");
                    q.addField("minpval_" + EfvTree.getEfEfvId(ef, efv) + "_dn");
                }
            }

            if (max < qstate.getEfos().getNumEfos()) {
                max = qstate.getEfos().getNumEfos();
            }
            for(String id : qstate.getEfos().getEfoIds())
            {
                String ide = EscapeUtil.encode(id);
                q.addField("cnt_efo_" + ide + "_up");
                q.addField("cnt_efo_" + ide + "_dn");
                q.addField("cnt_efo_" + ide + "_no");
                q.addField("minpval_efo_" + ide + "_up");
                q.addField("minpval_efo_" + ide + "_dn");
            }

            q.addField("score");
            q.addField("id");
            q.addField("name");
            q.addField("identifier");
            q.addField("species");
            q.addField("exp_info");
            for(String p : genePropService.getIdNameDescProperties())
                q.addField("property_" + p);
        } else {
            q.addField("*");
        }
        q.setFacetLimit(5 + max);

        q.setFacetMinCount(2);
        q.setFacetSort(FacetParams.FACET_SORT_COUNT);

        for(String p : genePropService.getDrilldownProperties()) {
            q.addFacetField("property_f_" + p);
        }

        q.addFacetField("species");
        q.addFacetField("exp_up_ids");
        q.addFacetField("exp_dn_ids");

        for(String ef : efvService.getFacetFactors())
        {
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
        for(String p : genePropService.getIdNameDescProperties())
            q.addHighlightField("property_" + p);
        return q;
    }

    private Iterable<FacetCounter> getGeneFacet(QueryResponse response, final String name, Set<String> values) {
        List<FacetCounter> facet = new ArrayList<FacetCounter>();
        FacetField ff = response.getFacetField(name);
        if(ff == null || ff.getValueCount() < 2 || ff.getValues() == null)
            return new ArrayList<FacetCounter>();

        for (FacetField.Count ffc : ff.getValues())
            if(!values.contains(ffc.getName()))
                facet.add(new FacetCounter(ffc.getName(), (int)ffc.getCount()));
        if(facet.size() < 2)
            return new ArrayList<FacetCounter>();

        Collections.sort(facet);
        return facet.subList(0, Math.min(facet.size(), 5));

    }

    private EfvTree<FacetUpDn> getEfvFacet(QueryResponse response, QueryState qstate) {
        EfvTree<FacetUpDn> efvFacet = new EfvTree<FacetUpDn>();
        Maker<FacetUpDn> creator = new Maker<FacetUpDn>() {
            public FacetUpDn make() { return new FacetUpDn(); }
        };
        for (FacetField ff : response.getFacetFields()) {
            if(ff.getValueCount() > 1) {
                if(ff.getName().startsWith("efvs_")) {
                    String ef = ff.getName().substring(8);
                    for (FacetField.Count ffc : ff.getValues())
                    {
                        if(!qstate.efvs.has(ef, ffc.getName()))
                        {
                            int count = (int)ffc.getCount();
                            efvFacet.getOrCreate(ef, ffc.getName(), creator)
                                    .add(count, ff.getName().substring(5,7).equals("up"));
                        }
                    }
                } else if(ff.getName().startsWith("exp_")) {
                    for (FacetField.Count ffc : ff.getValues())
                        if(!qstate.getExperiments().contains(ffc.getName()))
                        {
                            AtlasExperiment exp = atlasSolrDAO.getExperimentById(ffc.getName());
                            if(exp != null) {
                                String expName = exp.getAccession();
                                if(expName != null)
                                {
                                    int count = (int)ffc.getCount();
                                    efvFacet.getOrCreate(Constants.EXP_FACTOR_NAME, expName, creator)
                                            .add(count, ff.getName().substring(4,6).equals("up"));
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

    public List<String> getGenePropertyOptions() {
        List<String> result = new ArrayList<String>();
        for(String v :  genePropService.getIdNameDescProperties())
            result.add(v);
        result.add(Constants.GENE_PROPERTY_NAME);
        Collections.sort(result, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return atlasProperties.getCuratedGeneProperty(o1).compareToIgnoreCase(atlasProperties.getCuratedGeneProperty(o2));
            }
        });
        return result;
    }

    public SortedSet<String> getSpeciesOptions() {
        if(allSpecies.isEmpty()) {
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
            } catch(SolrServerException e) {
                throw new RuntimeException("Can't fetch all factors", e);
            }
        }
        return allSpecies;
    }

    public void onIndexBuildFinish(IndexBuilder builder, IndexBuilderEvent event) {
        allSpecies.clear();
    }

    public void onIndexBuildStart(IndexBuilder builder) {

    }

    public void destroy() throws Exception {
        if(indexBuilder != null)
            indexBuilder.unregisterIndexBuildEventHandler(this);
    }
}
