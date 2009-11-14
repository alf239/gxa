package uk.ac.ebi.gxa.index.builder;

import uk.ac.ebi.gxa.index.builder.listener.IndexBuilderListener;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;

/**
 * Interface for building a Gene Expression Atlas index.  Implementations should provide a way of setting the index
 * location, which may be of a generic type to allow the index to be backed by a database, file system, or some other
 * storage medium. IndexBuilder implementations should implement {@link #buildIndex()} which contains the logic to
 * construct the index.
 * <p/>
 * By default, all genes and experiments are included, and all experiments (both pending and non-pending) are included.
 * <p/>
 * If you are using an IndexBuilder in a standalone application (not a web application) and you do not want to reuse
 * IndexBuilder for multiple index building calls, you should make sure you register a listener that performs a {@link
 * #shutdown()} upon completion.  This will allow any resources being used by the IndexBuilder implementation to be
 * reclaimed.  Otherwise, an IndexBuilder instance may run indefinitely.
 *
 * @author Tony Burdett
 * @date 20-Aug-2009
 */
public interface IndexBuilder<T> {
    /**
     * Set the {@link uk.ac.ebi.microarray.atlas.dao.AtlasDAO} that will be used to obtain data to generate NetCDFs.
     *
     * @param atlasDAO the DAO that is used to obtain data to generate NetCDFs from
     */
    void setAtlasDAO(AtlasDAO atlasDAO);

    /**
     * Get the {@link uk.ac.ebi.microarray.atlas.dao.AtlasDAO} that will be used to obtain data to generate NetCDFs.
     *
     * @return the Atlas 2 compliant datasource to generate NetCDFs from
     */
    AtlasDAO getAtlasDAO();

    /**
     * Set the location for the index.  If there is already a pre-existing index at this location, implementations
     * should update this index.  If there is no index pre-existing, it should be created.
     *
     * @param indexLocation the location of the index
     */
    void setIndexLocation(T indexLocation);

    /**
     * Get the location of the index.  This may not exist, if the index builder has not yet been run.
     *
     * @return the location of the index
     */
    T getIndexLocation();

    /**
     * Flags that genes should be included in the construction of this index.
     * <p/>
     * This is true by default.
     *
     * @param genes whether to include genes in this build
     */
    void setIncludeGenes(boolean genes);

    boolean getIncludeGenes();

    /**
     * Flags that experiments should be included in the construction of this index.
     * <p/>
     * This is true by default.
     *
     * @param experiments whether to include experiments in this build
     */
    void setIncludeExperiments(boolean experiments);

    boolean getIncludeExperiments();

    /**
     * Initialise this IndexBuilder and any resources required by it.
     *
     * @throws IndexBuilderException if initialisation of this index builder failed for any reason
     */
    void startup() throws IndexBuilderException;

    /**
     * Shutdown this IndexBuilder, and release any resources used by it
     *
     * @throws IndexBuilderException if shutdown of this index builder failed for any reason
     */
    void shutdown() throws IndexBuilderException;

    /**
     * Build the index.  This will build the index entirely from scratch.  Use this if you wish to create or recreate
     * the index with up-to-date information from the backing database.
     * <p/>
     * Note that this method is not guaranteed to be synchronous, it only guarantees that the index has started
     * building. Implementations are free to define their own multithreaded strategies for index construction.  If you
     * wish to be notified on completion, you should register a listener to get callback events when the build completes
     * by using {@link #buildIndex(uk.ac.ebi.gxa.index.builder.listener.IndexBuilderListener)}. You can also use a
     * listener to get at any errors that may have occurred during index building.
     * <p/>
     * Calling this method is equivalent to calling <code>buildIndex(null)</code>.
     */
    void buildIndex();

    /**
     * Build the index and register a listener that provides a callback on completion of the build task.  This will
     * build the index entirely from scratch.  Use this if you wish to create or recreate the index with up-to-date
     * information from the backing database.
     * <p/>
     * Note that this method is not guaranteed to be synchronous, it only guarantees that the index has started
     * building. Implementations are free to define their own multithreaded strategies for index construction.
     * <p/>
     * The listener supplied will provide callbacks whenever the indexbuilder has interesting events to report.
     *
     * @param listener a listener that can be used to supply callbacks when building of the index completes, or when any
     *                 errors occur.
     */
    void buildIndex(IndexBuilderListener listener);

    /**
     * Incrementally builds the index, updating the existing index with new items rather than building from scratch.
     * <p/>
     * Note that this method only guarantees that the index has started updating. Implementations are free to define
     * their own multithreaded strategies for index construction, and should not block waiting for completion.  If you
     * wish to be notified on completion, you should register a listener to get callback events when the update
     * completes by using {@link #updateIndex(uk.ac.ebi.gxa.index.builder.listener.IndexBuilderListener)}. You can also
     * use a listener to get at any errors that may have occurred during index building.
     * <p/>
     * Calling this method is equivalent to calling <code>updateIndex(null)</code>.
     */
    void updateIndex();

    /**
     * Incrementally builds the index, updating the existing index with new items rather than building from scratch.
     * <p/>
     * Note that this method only guarantees that the index has started updating. Implementations are free to define
     * their own multithreaded strategies for index construction, and should not block waiting for completion.
     * <p/>
     * The listener supplied will provide callbacks whenever the indexbuilder has interesting events to report.
     *
     * @param listener a listener that can be used to supply callbacks when updating of the index completes, or when any
     *                 errors occur.
     */
    void updateIndex(IndexBuilderListener listener);
}
