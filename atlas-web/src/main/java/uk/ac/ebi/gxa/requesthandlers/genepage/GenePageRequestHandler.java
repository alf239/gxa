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
import ae3.model.AtlasGene;
import ae3.model.AtlasGeneDescription;
import ae3.service.structuredquery.UpdownCounter;
import org.springframework.web.HttpRequestHandler;
import uk.ac.ebi.gxa.efo.Efo;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.gxa.requesthandlers.base.ErrorResponseHelper;
import uk.ac.ebi.gxa.utils.FilterIterator;
import uk.ac.ebi.gxa.utils.EfvTree;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Gene page request handler 
 * @author pashky
 */
public class GenePageRequestHandler implements HttpRequestHandler {
    private AtlasSolrDAO atlasSolrDAO;
    private AtlasProperties atlasProperties;
    private Annotator annotator;
    private Efo efo;

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

    public void setEfo(Efo efo){
        this.efo = efo;
    }

    public Efo getEfo(){
        return efo;
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String geneId = request.getParameter("gid");
        String ef = request.getParameter("ef");

        if (geneId != null || !"".equals(geneId)) {
            AtlasSolrDAO.AtlasGeneResult result = atlasSolrDAO.getGeneByAnyIdentifier(geneId, atlasProperties.getGeneAutocompleteIdFields());
            if(result.isMulti()) {
                response.sendRedirect(request.getContextPath() + "/qrs?gprop_0=&gval_0="+geneId+"&fexp_0=UP_DOWN&fact_0=&specie_0=&fval_0=(all+conditions)&view=hm");
                return;
            }

            if(result.isFound()) {
                AnatomogramRequestHandler h= new AnatomogramRequestHandler();
                h.setAtlasSolrDAO(this.atlasSolrDAO);
                h.setEfo(this.efo);
                h.setAnnotator(new Annotator());


                h.handleRequest(request, null);
                request.setAttribute("anatomogramMap",h.getAnnotator().getMap());
                AtlasGene gene = result.getGene();
                request.setAttribute("orthologs", atlasSolrDAO.getOrthoGenes(gene));
                request.setAttribute("heatMapRows", gene.getHeatMap(atlasProperties.getGeneHeatmapIgnoredEfs()).getValueSortedList());
                request.setAttribute("differentiallyExpressedFactors",gene.getDifferentiallyExpressedFactors(atlasProperties.getGeneHeatmapIgnoredEfs(),atlasSolrDAO,ef));
                request.setAttribute("atlasGene", gene);
                request.setAttribute("ef", ef);
                request.setAttribute("atlasGeneDescription", new AtlasGeneDescription(atlasProperties, gene).toString());
                gene.setAnatomogramEfoList(annotator.getKnownEfo(Annotator.AnatomogramType.Web, gene.getGeneSpecies()));
                request.setAttribute("noAtlasExps", gene.getNumberOfExperiments());
                request.getRequestDispatcher("/WEB-INF/jsp/genepage/gene.jsp").forward(request,response);
                return;
            }
        }

        ErrorResponseHelper.errorNotFound(request, response, "There are no records for gene " + String.valueOf(geneId));
    }
}
