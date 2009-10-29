package uk.ac.ebi.ae3.indexbuilder.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.ae3.indexbuilder.Constants;
import uk.ac.ebi.ae3.indexbuilder.IndexBuilderException;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;
import uk.ac.ebi.microarray.atlas.model.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * An {@link IndexBuilderService} that generates index documents from the experiments in the Atlas database.
 * <p/>
 * Note that this implementation does NOT support updates - regardless of whether the update flag is set to true, this
 * will rebuild the index every time.
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
        List<Future<UpdateResponse>> tasks = new ArrayList<Future<UpdateResponse>>();

        try {
            for (final Experiment experiment : experiments) {
                tasks.add(tpool.submit(new Callable<UpdateResponse>() {
                    public UpdateResponse call() throws IOException, SolrServerException {
                        // Create a new solr document
                        SolrInputDocument solrInputDoc = new SolrInputDocument();

                        // Add field "exp_in_dw" = true, to show this experiment is present
                        getLog().info("Updating index - adding experiment " + experiment.getAccession());
                        getLog().debug("Adding standard fields for experiment stats");
                        solrInputDoc.addField(Constants.FIELD_EXP_IN_DW,
                                              true);
                        solrInputDoc.addField(Constants.FIELD_DWEXP_ID,
                                              experiment.getExperimentID());
                        solrInputDoc.addField(Constants.FIELD_DWEXP_ACCESSION,
                                              experiment.getAccession());
                        solrInputDoc.addField(Constants.FIELD_DWEXP_EXPDESC,
                                              experiment.getDescription());

                        // now, fetch assays for this experiment
                        List<Assay> assays = getAtlasDAO().getAssaysByExperimentAccession(experiment.getAccession());
                        if (assays.size() == 0) {
                            getLog().warn("No assays present for " +
                                    experiment.getAccession());
                        }

                        for (Assay assay : assays) {
                            // get assay properties and values
                            for (Property prop : assay.getProperties()) {
                                String p = prop.getName();
                                String pv = prop.getValue();

                                getLog().trace("Updating index, assay property " + p + " = " + pv);
                                solrInputDoc.addField(Constants.PREFIX_DWE + p, pv);
                                getLog().trace("Wrote " + p + " = " + pv);
                            }
                        }

                        // now get samples
                        List<Sample> samples = getAtlasDAO().getSamplesByExperimentAccession(experiment.getAccession());
                        if (samples.size() == 0) {
                            getLog().warn("No samples present for experiment " + experiment.getAccession());
                        }

                        for (Sample sample : samples) {
                            // get sample properties and values
                            for (Property prop : sample.getProperties()) {
                                String p = prop.getName();
                                String pv = prop.getValue();

                                getLog().trace("Updating index, sample property " + p + " = " + pv);
                                solrInputDoc.addField(Constants.PREFIX_DWE + p, pv);
                                getLog().trace("Wrote " + p + " = " + pv);
                            }
                        }

                        // now, fetch atlas counts for this experiment
                        getLog().debug("Evaluating atlas counts for " + experiment.getAccession());
                        List<AtlasCount> atlasCounts = getAtlasDAO().getAtlasCountsByExperimentID(
                                experiment.getExperimentID());
                        getLog().debug(
                                experiment.getAccession() + " has " + atlasCounts.size() + " atlas count objects");
                        for (AtlasCount count : atlasCounts) {
                            // encode values in UTF-8 format for indexing
                            String ef = URLEncoder.encode(count.getProperty(), "UTF-8");
                            String efv = URLEncoder.encode(count.getPropertyValue(), "UTF-8");
                            // efvid is concatenation of ef and efv
                            String efvid = ef + "_" + efv;
                            // field name is efvid_up / efvid_dn depending on expression
                            String fieldname = efvid + "_" + (count.getUpOrDown().equals("-1") ? "dn" : "up");

                            // add a field:
                            // key is the fieldname, value is the total count
                            getLog().debug("Updating index with atlas count data... key: " + fieldname + "; " +
                                    "value: " + count.getGeneCount());
                            solrInputDoc.addField(fieldname, count.getGeneCount());
                        }

                        // finally, add the document to the index
                        getLog().info("Finalising changes for " + experiment.getAccession());
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
                        throw new IndexBuilderException("An error occurred updating Experiments SOLR index", e);
                    }
                }
                catch (InterruptedException e) {
                    throw new IndexBuilderException("An error occurred updating Experiments SOLR index", e);
                }
            }
        }
        finally {
            // shutdown the service
            tpool.shutdown();
        }
    }
}
