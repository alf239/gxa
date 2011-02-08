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

package uk.ac.ebi.gxa.loader.service;

import org.mged.magetab.error.ErrorCode;
import org.mged.magetab.error.ErrorItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABArrayDesign;
import uk.ac.ebi.arrayexpress2.magetab.exception.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.handler.HandlerPool;
import uk.ac.ebi.arrayexpress2.magetab.handler.ParserMode;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.impl.AccessionHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.impl.ArrayDesignNameHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.impl.ProviderHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.impl.TechnologyTypeHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.node.CompositeElementHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.adf.node.ReporterHandler;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABArrayParser;
import uk.ac.ebi.gxa.dao.AtlasDAO;
import uk.ac.ebi.gxa.dao.LoadStage;
import uk.ac.ebi.gxa.dao.LoadStatus;
import uk.ac.ebi.gxa.dao.LoadType;
import uk.ac.ebi.gxa.loader.AtlasLoaderException;
import uk.ac.ebi.gxa.loader.DefaultAtlasLoader;
import uk.ac.ebi.gxa.loader.LoadArrayDesignCommand;
import uk.ac.ebi.gxa.loader.cache.AtlasLoadCache;
import uk.ac.ebi.gxa.loader.cache.AtlasLoadCacheRegistry;
import uk.ac.ebi.gxa.loader.handler.adf.*;
import uk.ac.ebi.gxa.loader.utils.AtlasLoaderUtils;
import uk.ac.ebi.microarray.atlas.model.ArrayDesignBundle;
import uk.ac.ebi.microarray.atlas.model.LoadDetails;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22-Feb-2010
 */
public class AtlasArrayDesignLoader {
    
    protected AtlasDAO atlasDAO;

    private boolean allowReloading = false;

        // logging
    final private Logger log = LoggerFactory.getLogger(this.getClass());
    
    public void process(final LoadArrayDesignCommand cmd, final AtlasLoaderServiceListener listener) throws AtlasLoaderException {
        final URL adfFileLocation = cmd.getUrl();

        // create a cache for our objects
        AtlasLoadCache cache = new AtlasLoadCache();

        // create an investigation ready to parse to
        final MAGETABArrayDesign arrayDesign = new MAGETABArrayDesign();

        // pair this cache and this investigation in the registry
        AtlasLoadCacheRegistry.getRegistry().registerArrayDesign(arrayDesign, cache);

        try {
            // configure the handlers so we write out the right bits
            configureHandlers();

            // now, perform the parse - with registered handlers, our cache will be populated
            MAGETABArrayParser parser = new MAGETABArrayParser();
            parser.setParsingMode(ParserMode.READ_AND_WRITE);

            // register an error item listener
            parser.addErrorItemListener(new ErrorItemListener() {
                public void errorOccurred(ErrorItem item) {
                    // lookup message
                    String message = "";
                    for (ErrorCode ec : ErrorCode.values()) {
                        if (item.getErrorCode() == ec.getIntegerValue()) {
                            message = ec.getErrorMessage();
                            break;
                        }
                    }
                    if (message.equals("")) {
                        if (item.getComment().equals("")) {
                            message = "Unknown error";
                        }
                        else {
                            message = item.getComment();
                        }
                    }
                    String comment = item.getComment();

                    // log the error
                    // todo: this should go to a different log stream, part of loader report -
                    // probably should dynamically creating an appender that writes to the magetab directory
                    log.error(
                            "Parser reported:\n\t" +
                                    item.getErrorCode() + ": " + message + " (" + comment + ")\n\t\t- " +
                                    "occurred in parsing " + item.getParsedFile() + " " +
                                    "[line " + item.getLine() + ", column " + item.getCol() + "].");
                }
            });

            AtlasLoaderUtils.WatcherThread watcher = AtlasLoaderUtils.createProgressWatcher(arrayDesign, listener);
            try {
                parser.parse(adfFileLocation, arrayDesign);
                log.info("Parsing finished");
            }
            catch (ParseException e) {
                // something went wrong - no objects have been created though
                log.error("There was a problem whilst trying to parse " + adfFileLocation, e);
                throw new AtlasLoaderException(e);
            } finally {
                if(watcher != null)
                    watcher.stopWatching();
            }


            if (listener != null) {
                listener.setProgress("Storing array design to DB");
            }

            // parsing completed, so now write the objects in the cache
            try {
                writeObjects(cache, cmd.getGeneIdentifierPriority());

                if (listener != null) {
                    if (cache.fetchArrayDesignBundle() != null) {
                        listener.setAccession(cache.fetchArrayDesignBundle().getAccession());
                    }
                }
            } catch (AtlasLoaderException e) {
                throw e;
            } catch (Exception e) {
                throw new AtlasLoaderException(e);
            }
        }
        finally {
            AtlasLoadCacheRegistry.getRegistry().deregisterArrayDesign(arrayDesign);
            cache.clear();
        }
    }

    protected void configureHandlers() {
        HandlerPool pool = HandlerPool.getInstance();

        pool.replaceHandlerClass(AccessionHandler.class,
                                 AtlasLoadingAccessionHandler.class);
        pool.replaceHandlerClass(CompositeElementHandler.class,
                                 AtlasLoadingCompositeElementHandler.class);
        pool.replaceHandlerClass(ArrayDesignNameHandler.class,
                                 AtlasLoadingNameHandler.class);
        pool.replaceHandlerClass(ProviderHandler.class,
                                 AtlasLoadingProviderHandler.class);
        pool.replaceHandlerClass(TechnologyTypeHandler.class,
                                 AtlasLoadingTypeHandler.class);
        pool.replaceHandlerClass(ReporterHandler.class,
                                AtlasLoadingReporterHandler.class);
    }

    protected void writeObjects(AtlasLoadCache cache, Collection<String> priority) throws AtlasLoaderException {
        int numOfObjects = cache.fetchArrayDesignBundle() == null ? 0 : 1;

        // validate the load(s)
        validateLoad(cache.fetchArrayDesignBundle());

        // start the load(s)
        boolean success = false;
        startLoad(cache.fetchArrayDesignBundle().getAccession());

        try {
            // write the data
            log.info("Writing " + numOfObjects + " objects to Atlas 2 datasource...");

            long start, end;
            String total;

            // load array design bundles
            start = System.currentTimeMillis();
            log.info("Writing array design " + cache.fetchArrayDesignBundle().getAccession());
            // first, update the bundle with the identifier preferences
            cache.fetchArrayDesignBundle().setGeneIdentifierNamesInPriorityOrder(priority);

            getAtlasDAO().writeArrayDesignBundle(cache.fetchArrayDesignBundle());
            end = System.currentTimeMillis();
            total = new DecimalFormat("#.##").format((end - start) / 1000);
            log.info("Wrote array design {} in {}s.", cache.fetchArrayDesignBundle().getAccession(), total);

            // and return true - everything loaded ok
            log.info("Writing " + numOfObjects + " objects completed successfully");
            success = true;
        }
        catch (Exception e) {
            throw new AtlasLoaderException(e);
        }
        finally {
            // end the load(s)
            endLoad(cache.fetchArrayDesignBundle().getAccession(), success);
        }
    }

    private void validateLoad(ArrayDesignBundle arrayDesignBundle) throws AtlasLoaderException {
        if (arrayDesignBundle == null) {
            String msg = "No array design created - unable to load";
            log.error(msg);
            throw new AtlasLoaderException(msg);
        }

        checkArrayDesign(arrayDesignBundle.getAccession());

        // all checks passed if we got here
    }

    private void checkArrayDesign(String accession) throws AtlasLoaderException {
        // check load_monitor for this accession
        log.debug("Fetching load details for " + accession);
        LoadDetails loadDetails = getAtlasDAO().getLoadDetailsForArrayDesignsByAccession(accession);
        if (loadDetails != null) {
            log.info("Found load details for " + accession);
            // if we are suppressing reloads, check the details further
            if (!allowReloading()) {
                log.info("Load details present, reloads not allowed...");
                // there are details: load is valid only if the load status is "pending" or "failed"
                boolean pending = loadDetails.getStatus().equalsIgnoreCase(LoadStatus.PENDING.toString());
                if(pending)
                    throw new AtlasLoaderException("Array design is in PENDING state");

                boolean priorFailure = loadDetails.getStatus().equalsIgnoreCase(LoadStatus.FAILED.toString());
                if (priorFailure) {
                    String msg = "Array Design " + accession + " was previously loaded, but failed.  " +
                            "Any bad data will be overwritten";
                    log.warn(msg);
                    throw new AtlasLoaderException(msg);
                }
            }
            else {
                // not suppressing reloads, so continue
                log.warn("Array Design " + accession + " was previously loaded, but reloads are not " +
                        "automatically suppressed");
            }
        }
        else {
            // no experiment present in load_monitor table
            log.debug("No load details obtained");
        }
    }

    private void startLoad(String accession) {
        log.info("Updating load_monitor: starting load for " + accession);
        getAtlasDAO().writeLoadDetails(accession,
                                       LoadStage.LOAD,
                                       LoadStatus.WORKING,
                                       LoadType.ARRAYDESIGN);
    }

    private void endLoad(String accession, boolean success) {
        log.info("Updating load_monitor: ending load for " + accession);
        getAtlasDAO().writeLoadDetails(accession,
                                       LoadStage.LOAD,
                                       success ? LoadStatus.DONE : LoadStatus.FAILED,
                                       LoadType.ARRAYDESIGN);
    }

    public AtlasDAO getAtlasDAO() {
        if (atlasDAO == null) {
            throw new IllegalStateException("atlasDAO is not set.");
        }
        return atlasDAO;
    }

    public void setAtlasDAO(AtlasDAO atlasDAO) {
        this.atlasDAO = atlasDAO;
    }

    public boolean allowReloading() {
        return allowReloading;
    }

    public void setAllowReloading(boolean allowReloading) {
        this.allowReloading = allowReloading;
    }
}
