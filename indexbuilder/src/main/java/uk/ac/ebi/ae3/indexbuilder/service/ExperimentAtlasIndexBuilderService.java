package uk.ac.ebi.ae3.indexbuilder.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.ae3.indexbuilder.Constants;
import uk.ac.ebi.ae3.indexbuilder.IndexBuilderException;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;
import uk.ac.ebi.microarray.atlas.model.Assay;
import uk.ac.ebi.microarray.atlas.model.Experiment;
import uk.ac.ebi.microarray.atlas.model.Property;
import uk.ac.ebi.microarray.atlas.model.Sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * An {@link IndexBuilderService} that generates index documents from the
 * experiments in the Atlas database.
 * <p/>
 * Note that this implementation does NOT support updates - regardless of
 * whether the update flag is set to true, this will rebuild the index every
 * time.
 *
 * @author Tony Burdett
 * @date 22-Sep-2009
 */
public class ExperimentAtlasIndexBuilderService extends IndexBuilderService {
  private static final int NUM_THREADS = 64;

  public ExperimentAtlasIndexBuilderService(AtlasDAO atlasDAO,
                                            SolrServer solrServer) {
    super(atlasDAO, solrServer);
  }

  protected void createIndexDocs() throws IndexBuilderException {
    // do initial setup - build executor service
    ExecutorService tpool = Executors.newFixedThreadPool(NUM_THREADS);

    // fetch experiments - check if we want all or only the pending ones
    List<Experiment> experiments = getPendingOnly()
        ? getAtlasDAO().getAllExperimentsPendingIndexing()
        : getAtlasDAO().getAllExperiments();

    // the list of futures - we need these so we can block until completion
    List<Future<UpdateResponse>> tasks =
        new ArrayList<Future<UpdateResponse>>();

    try {
      for (final Experiment experiment : experiments) {
        tasks.add(tpool.submit(new Callable<UpdateResponse>() {
          public UpdateResponse call() throws IOException, SolrServerException {
            // Create a new solr document
            SolrInputDocument solrInputDoc = new SolrInputDocument();

            // Add field "exp_in_dw" = true, to show this experiment is present
            getLog().info(
                "Updating index - experiment " + experiment.getAccession() +
                    " is in DB");
            solrInputDoc.addField(Constants.FIELD_EXP_IN_DW,
                                  true);
            solrInputDoc.addField(Constants.FIELD_DWEXP_ID,
                                  experiment.getExperimentID());
            solrInputDoc.addField(Constants.FIELD_DWEXP_ACCESSION,
                                  experiment.getAccession());
            solrInputDoc.addField(Constants.FIELD_DWEXP_EXPDESC,
                                  experiment.getDescription());

            // now, fetch assays for this experiment
            List<Assay> assays = getAtlasDAO().getAssaysByExperimentAccession(
                experiment.getAccession());
            if (assays.size() == 0) {
              getLog().warn(
                  "No assays present for " + experiment.getAccession());
            }

            for (Assay assay : assays) {
              // get assay properties and values
              for (Property prop : assay.getProperties()) {
                String p = prop.getName();
                String pv = prop.getValue();

                getLog().info(
                    "Updating index, assay property " + p + " = " + pv);
                solrInputDoc.addField(Constants.PREFIX_DWE + p, pv);
              }

              // now get samples
              List<Sample> samples = getAtlasDAO().getSamplesByAssayAccession(
                  assay.getAccession());
              if (samples.size() == 0) {
                getLog().warn(
                    "No samples present for assay " + assay.getAccession());
              }

              for (Sample sample : samples) {
                // get sample properties and values
                for (Property prop : sample.getProperties()) {
                  String p = prop.getName();
                  String pv = prop.getValue();

                  getLog().info(
                      "Updating index, sample property " + p + " = " + pv);
                  solrInputDoc.addField(Constants.PREFIX_DWE + p, pv);
                }
              }
            }

            // now, fetch gene counts for this experiment
            // todo - need big join across experiment/arraydesign/designelement/gene
            


            // finally, add the document to the index
            getLog().info("Finalising changes for " +
                experiment.getAccession());
            return getSolrServer().add(solrInputDoc);
          }
        }));
      }

      // block until completion, and throw any errors
      for (Future<UpdateResponse> task : tasks) {
        try {
          task.get();
        }
        catch (ExecutionException e) {
          if (e.getCause() instanceof IndexBuilderException) {
            throw (IndexBuilderException) e.getCause();
          }
          else {
            throw new IndexBuilderException(
                "An error occurred updating Experiments SOLR index", e);
          }
        }
        catch (InterruptedException e) {
          throw new IndexBuilderException(
              "An error occurred updating Experiments SOLR index", e);
        }
      }
    }
    finally {
      // shutdown the service
      tpool.shutdown();
    }
  }
}
