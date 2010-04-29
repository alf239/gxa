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

package uk.ac.ebi.gxa.requesthandlers.genepage;

import ae3.anatomogram.Annotator;
import ae3.dao.AtlasSolrDAO;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import ae3.model.AtlasGene;
import uk.ac.ebi.gxa.requesthandlers.base.ErrorResponseHelper;
import uk.ac.ebi.gxa.properties.AtlasProperties;

/**
 * Gene page request handler 
 * @author pashky
 */
public class GenePageRequestHandler implements HttpRequestHandler {
    private AtlasSolrDAO atlasSolrDAO;
    private AtlasProperties atlasProperties;
    private Annotator annotator;

    public Annotator getAnnotator() {
        return annotator;
    }

    public void setAnnotator(Annotator annotator) {
        this.annotator = annotator;
    }

    public AtlasSolrDAO getAtlasSolrDao() {
        return atlasSolrDAO;
    }

    public void setAtlasSolrDao(AtlasSolrDAO atlasSolrDAO) {
        this.atlasSolrDAO = atlasSolrDAO;
    }

    public void setAtlasProperties(AtlasProperties atlasProperties) {
        this.atlasProperties = atlasProperties;
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String geneId = request.getParameter("gid");

        if (geneId != null || !"".equals(geneId)) {
            AtlasSolrDAO.AtlasGeneResult result = atlasSolrDAO.getGeneByIdentifier(geneId);
            if(result.isMulti()) {
                response.sendRedirect(request.getContextPath() + "/qrs?gprop_0=&gval_0="+geneId+"&fexp_0=UP_DOWN&fact_0=&specie_0=&fval_0=(all+conditions)&view=hm");
                return;
            }

            if(result.isFound()) {
                AtlasGene gene = result.getGene();
                request.setAttribute("orthologs", atlasSolrDAO.getOrthoGenes(gene));
                request.setAttribute("heatMapRows", gene.getHeatMapRows(atlasProperties.getGeneHeatmapIgnoredEfs()));
                request.setAttribute("atlasGene", gene);
                gene.setAnatomogramEfoList(annotator.getKnownEfo());
                request.setAttribute("noAtlasExps", gene.getNumberOfExperiments());
                request.getRequestDispatcher("/WEB-INF/jsp/genepage/gene.jsp").forward(request,response);
                return;
            }

        }

        ErrorResponseHelper.errorNotFound(request, response, "There are no records for gene " + String.valueOf(geneId));
    }
}
