package uk.ac.ebi.gxa.index.builder.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.gxa.index.builder.IndexBuilderException;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;

import java.io.IOException;

/**
 * An abstract IndexBuilderService, that provides convenience methods for getting and setting parameters required across
 * all SOLR index building implementations.  This class contains a single method, {@link #buildIndex(boolean)} that clients
 * should use to construct the different types of index in a consistent manner.  Implementing classes have access to an
 * {@link org.apache.solr.client.solrj.embedded.EmbeddedSolrServer} to update the index, and an {@link
 * uk.ac.ebi.microarray.atlas.dao.AtlasDAO} that provides interaction with the Atlas database (following an Atlas 2
 * schema).
 * <p/>
 * All implementing classes should implement the method {@link #createIndexDocs(boolean)} which contains the logic for
 * constructing the relevant parts of the index for each implementation.  Implementations do not need to be concerned
 * with the SOLR index lifecycle, as this is handled by this abstract classes and {@link
 * uk.ac.ebi.gxa.index.builder.IndexBuilder} implementations.
 *
 * @author Miroslaw Dylag (original version)
 * @author Tony Burdett (atlas 2 revision)
 */
public abstract class IndexBuilderService {
    private AtlasDAO atlasDAO;
    private SolrServer solrServer;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public IndexBuilderService(AtlasDAO atlasDAO, SolrServer solrServer) {
        this.atlasDAO = atlasDAO;
        this.solrServer = solrServer;
    }

    protected AtlasDAO getAtlasDAO() {
        return atlasDAO;
    }

    protected SolrServer getSolrServer() {
        return solrServer;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * Build the index for this particular IndexBuilderService implementation. Once the index has been built, this
     * method will automatically commit any changes and release any resources held by the SOLR server.
     *
     * @param pendingOnly only index items that are flagged as pending in the database if true, if false include
     *                    everything
     * @throws IndexBuilderException if the is a problem whilst generating the index
     */
    public void buildIndex(boolean pendingOnly) throws IndexBuilderException {
        try {
            createIndexDocs(pendingOnly);
            solrServer.commit();
        }
        catch (IOException e) {
            throw new IndexBuilderException(
                    "Cannot commit changes to the SOLR server", e);
        }
        catch (SolrServerException e) {
            throw new IndexBuilderException(
                    "Cannot commit changes to the SOLR server - server threw exception",
                    e);
        }
    }

    /**
     * Generate the required documents for the SOLR index, as appropriate to this implementation.  This method blocks
     * until all index documents have been created.
     * <p/>
     * Implementations are free to define their own optimization strategy, and it is acceptable to use asynchronous
     * operations.
     *
     * @param pendingOnly only index items that are flagged as pending in the database if true, if false include
     *                    everything
     * @throws uk.ac.ebi.gxa.index.builder.IndexBuilderException
     *          if there is a problem whilst trying to generate the index documents
     */
    protected abstract void createIndexDocs(boolean pendingOnly) throws IndexBuilderException;
}
