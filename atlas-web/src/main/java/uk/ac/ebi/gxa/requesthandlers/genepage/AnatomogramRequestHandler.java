package uk.ac.ebi.gxa.requesthandlers.genepage;

import ae3.anatomogram.Annotator;
import ae3.dao.AtlasSolrDAO;
import ae3.model.AtlasGene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import uk.ac.ebi.gxa.dao.AtlasDAO;
import uk.ac.ebi.gxa.efo.Efo;
import uk.ac.ebi.gxa.efo.EfoTerm;
import uk.ac.ebi.gxa.requesthandlers.base.ErrorResponseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Andrey
 * Date: Mar 25, 2010
 * Time: 1:37:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnatomogramRequestHandler implements HttpRequestHandler {
    private AtlasSolrDAO atlasSolrDAO;
    private Efo efo;
    private Annotator annotator;
    private String organism;
    private Annotator.AnatomogramType anatomogramType = Annotator.AnatomogramType.Das;

    public AtlasSolrDAO getAtlasSolrDAO() {
        return atlasSolrDAO;
    }

    public void setAtlasSolrDAO(AtlasSolrDAO atlasSolrDAO) {
        this.atlasSolrDAO = atlasSolrDAO;
    }

    public Efo getEfo() {
        return efo;
    }

    public void setEfo(Efo efo) {
        this.efo = efo;
    }

    public Annotator getAnnotator() {
        return annotator;
    }

    public void setAnnotator(Annotator annotator) {
        this.annotator = annotator;
    }

    public static class Annotation {
        public Annotation(String id, String caption, int up, int dn) {
            this.id = id;
            this.caption = caption;
            this.up = up;
            this.dn = dn;
        }

        public String id;
        public String caption;
        public int up;
        public int dn;
    }
    public static Annotation newAnnotation(String id, String caption, int up, int dn){
        return new Annotation(id,caption,up,dn);
    }

    public List<Annotation> getAnnotations(String geneIdentifier) {
        List<Annotation> result = new ArrayList<Annotation>();

        AtlasSolrDAO.AtlasGeneResult geneResult = getAtlasSolrDAO().getGeneByIdentifier(geneIdentifier);
        if (geneResult.isFound()) {
            AtlasGene gene = geneResult.getGene();

            /*Arrays.asList("EFO_0000302","EFO_0000792","EFO_0000800","EFO_0000943","EFO_0000110"
                ,"EFO_0000265","EFO_0000815","EFO_0000803","EFO_0000793","EFO_0000827"
                ,"EFO_0000889","EFO_0000934","EFO_0000935","EFO_0000968","EFO_0001385","EFO_0001412"
                ,"EFO_0001413","EFO_0001937")*/
            this.organism = gene.getGeneSpecies();

            for (String acc : annotator.getKnownEfo(this.anatomogramType, this.organism)) {
                
                EfoTerm term = getEfo().getTermById(acc);

                int dn = gene.getCount_dn(acc);
                int up = gene.getCount_up(acc);

                if((dn>0)||(up>0))
                    result.add(new Annotation(acc, term.getTerm(), up, dn));
            }
        }
        else{//not found
            return null;
            ///this.organism = "unknown";
            ///throw new IllegalArgumentException(String.format("gene not found : %1$s",geneIdentifier));
        }

        return result;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String geneId = request.getParameter("gid");
        this.anatomogramType = Annotator.AnatomogramType.Das;

        if(null != request.getParameter("type"))
            if (0 == request.getParameter("type").compareToIgnoreCase("web")) {
                this.anatomogramType = Annotator.AnatomogramType.Web;
            }

        if ((null!=geneId)&&(!"".equals(geneId))) {
            try {
                List<Annotation> annotations = getAnnotations(geneId);
                if((null == annotations)||(annotations.size()==0)) {
                    if(null != response)
                        response.setContentType("image/png");
                        annotator.getEmptyPicture(Annotator.Encoding.Png, response.getOutputStream());
                    return;
                }
                if(null == response) {
                    annotator.process(this.organism, annotations, Annotator.Encoding.Png /*Png,Jpeg*/, null, this.anatomogramType);
                } else {
                    response.setContentType("image/png");
                    annotator.process(this.organism, annotations, Annotator.Encoding.Png /*Png,Jpeg*/, response.getOutputStream(),this.anatomogramType);
                }
            } catch(IllegalArgumentException e) {
                log.info("Failed to process anatomogram: " + e.getMessage());
            } catch(Exception ex) {
                log.error("Error!",ex);
            }
        } else {
            ErrorResponseHelper.errorNotFound(request, response, "Cannot process anatomogram request without a gene identifier!");
        }
    }
}
