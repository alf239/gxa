package ae3.servlet.structuredquery;

import ae3.service.structuredquery.*;
import ae3.servlet.structuredquery.result.ErrorResult;
import ae3.servlet.structuredquery.result.ExperimentRestProfile;
import ae3.servlet.structuredquery.result.ExperimentResultAdapter;
import ae3.servlet.structuredquery.result.HeatmapResultAdapter;
import ae3.model.AtlasExperiment;
import ae3.model.AtlasGene;
import ae3.dao.AtlasDao;
import org.apache.commons.lang.NotImplementedException;
import uk.ac.ebi.gxa.web.Atlas;
import uk.ac.ebi.gxa.web.AtlasSearchService;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;
import uk.ac.ebi.microarray.atlas.model.Experiment;
import uk.ac.ebi.microarray.atlas.model.Gene;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * REST API structured query servlet. Handles all gene and experiment API queries according to HTTP request parameters
 *
 * @author pashky
 */
public class ApiStructuredQueryServlet extends RestServlet {

    @Override
    public Object process(HttpServletRequest request) {
        final String experimentId = request.getParameter("experiment");

        // fetch search service from the session context
        AtlasSearchService searchService =
                (AtlasSearchService) getServletContext().getAttribute(Atlas.SEARCH_SERVICE.key());

        if (experimentId != null) {
            AtlasDao dao = searchService.getAtlasSolrDAO();
            AtlasExperiment exp = dao.getExperimentByAccession(experimentId);
            if (exp == null) {
                exp = dao.getExperimentById(experimentId);
            }
            if (exp == null) {
                Collection<AtlasExperiment> exps = dao.getExperimentsByQuery(experimentId, 0, 5000);
                if (exps.isEmpty()) {
                    return new ErrorResult("No such experiment found for: " + experimentId);
                }
                else {
                    return exps;
                }
            }
            else {
                List<AtlasGene> genes = new ArrayList<AtlasGene>();
                final String[] geneIds = request.getParameterValues("gene");
                if (geneIds != null) {
                    if (geneIds.length == 1 && geneIds[0].startsWith("top")) {
                        int nTop;
                        try {
                            nTop = Integer.valueOf(geneIds[0].substring(3));
                            if (nTop > 100) {
                                nTop = 100;
                            }
                        }
                        catch (NumberFormatException e) {
                            // if geneIds gives NaN
                            log.warn("Couldn't evaluate integer value of '" + geneIds[0].substring(3) +
                                    "' limiting to 10 values");
                            nTop = 10;
                        }

                        for (StructuredResultRow r : searchService.getAtlasQueryService().findGenesForExperiment(
                                "", experimentId, 0, nTop).getResults()) {
                            genes.add(r.getGene());
                        }
                    }
                    else {
                        for (String geneId : geneIds) {
                            AtlasDao.AtlasGeneResult agr = dao.getGeneByIdentifier(geneId);
                            if (agr.isFound() && !genes.contains(agr.getGene())) {
                                genes.add(agr.getGene());
                            }
                        }
                    }
                }
                setRestProfile(ExperimentRestProfile.class);
                return new ExperimentResultAdapter(exp, genes);
            }
        }
        else {
            AtlasStructuredQueryService asqs = searchService.getAtlasQueryService();

            AtlasStructuredQuery atlasQuery = AtlasStructuredQueryParser.parseRestRequest(
                    request, asqs.getGenePropertyOptions(), asqs.getExperimentalFactors());

            if (!atlasQuery.isNone()) {
                atlasQuery.setFullHeatmap(true);
                atlasQuery.setViewType(ViewType.HEATMAP);
                AtlasStructuredQueryResult atlasResult = asqs.doStructuredAtlasQuery(atlasQuery);
                return new HeatmapResultAdapter(atlasResult, searchService.getAtlasSolrDAO());
            }
            else {
                return new ErrorResult("Empty query specified");
            }
        }
    }

}
