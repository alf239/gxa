package uk.ac.ebi.gxa.loader.utils;

import org.apache.commons.logging.Log;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.Status;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.gxa.loader.cache.AtlasLoadCache;
import uk.ac.ebi.gxa.loader.cache.AtlasLoadCacheRegistry;
import uk.ac.ebi.microarray.atlas.model.Assay;
import uk.ac.ebi.microarray.atlas.model.Experiment;
import uk.ac.ebi.microarray.atlas.model.Sample;

/**
 * Simple utilities classes dealing with common functions that are required in loading to the Atlas DB.
 *
 * @author Tony Burdett
 * @date 26-Aug-2009
 */
public class AtlasLoaderUtils {
    /**
     * Blocking method that waits until the IDF for the given MAGETABInvestigation reached {@link
     * Status}<code>.COMPILING</code> status, or else fails.
     *
     * @param investigation the investigation to wait on
     * @param handlerName   the name of the handler (or the client class) that is waiting - this is used for logging
     * @param log           a log stream to write - this is used in debug modes
     * @return true when the IDF has finished compiling, or false if it failed
     */
    public static boolean waitWhilstIDFCompiles(final MAGETABInvestigation investigation, String handlerName, Log log) {
        // compile objects
        while (investigation.IDF.getStatus().ordinal() < Status.COMPILING.ordinal()
                && investigation.getStatus() != Status.FAILED) {
            synchronized (investigation.IDF) {
                try {
                    investigation.IDF.wait(1000);
                    log.trace(handlerName + " polling for status");
                }
                catch (InterruptedException e) {
                    // ignore this
                }
            }
        }

        // exited the loop, check whether this is due to fail or complete
        return investigation.getStatus() != Status.FAILED;
    }

    public static SDRFNode waitForSDRFNode(String nodeName,
                                           String nodeType,
                                           final MAGETABInvestigation investigation,
                                           String handlerName,
                                           Log log) throws LookupException {
        if (nodeName == null) {
            throw new LookupException("Cannot lookup an object using a null nodeName");
        }

        log.debug(handlerName + " doing lookup for " + nodeType + " " + nodeName);
        log.trace("Thread [" + Thread.currentThread().getName() + "] polling for dependent object");
        // fetch from the bag
        while (investigation.SDRF.lookupNode(nodeName, nodeType) == null &&
                investigation.SDRF.getStatus().ordinal() < Status.COMPILING.ordinal() &&
                investigation.getStatus() != Status.FAILED) {
            // object isn't in the bag yet, so wait
            synchronized (investigation) {
                try {
                    log.trace("Thread [" + Thread.currentThread().getName() + "] waiting, no result yet");
                    // wait for new objects to be available
                    investigation.wait(1000);
                    log.trace("Thread [" + Thread.currentThread().getName() + "] resumed");
                }
                catch (InterruptedException e) {
                    if (investigation.getStatus() == Status.FAILED) {
                        log.warn(handlerName + " was interrupted by a failure elsewhere " +
                                "whilst waiting for " + nodeType + " " + nodeName + " and is terminating");
                        throw new LookupException(
                                "Interrupted by a fail whilst waiting " + "for " + nodeType + " " + nodeName);
                    }
                    else {
                        // interrupted but no fail, so safe to continue
                    }
                }
            }
        }
        log.debug(handlerName + " resumed after dependent object obtained");
        return investigation.SDRF.lookupNode(nodeName, nodeType);
    }

    /**
     * Blocking method that waits until an experiment with the given accession number is available.  Note that this
     * method will be interrupted if the investigation acquires a {@link uk.ac.ebi.arrayexpress2.magetab.datamodel.Status}<code>.FAILED</code>
     * status, but not otherwise.  If the calling code never writes an experiment with this accession into a cache
     * associated with the given investigation, this method will never terminate.
     *
     * @param accession     the accession of the experiment to wait for
     * @param investigation the investigation to wait on.  This investigation is used to retrieve an {@link
     *                      uk.ac.ebi.gxa.loader.cache.AtlasLoadCache} from the {@link uk.ac.ebi.gxa.loader.cache.AtlasLoadCacheRegistry}
     *                      singleton.
     * @param handlerName   the name of the handler (or the client class) that is waiting - this is used for logging
     * @param log           a log stream to write - this is used in debug modes
     * @return true when the IDF has finished compiling, or false if it failed
     * @throws LookupException if lookup failed - for example, if the accession supplied was null, or if the
     *                         investigation acquires a failed status while this method was waiting.
     */
    public static Experiment waitForExperiment(String accession,
                                               final MAGETABInvestigation investigation,
                                               String handlerName,
                                               Log log) throws LookupException {
        // retrieve object bag
        AtlasLoadCache cache = AtlasLoadCacheRegistry.getRegistry().retrieveAtlasLoadCache(investigation);

        // check the identifier is not null
        if (accession == null) {
            throw new LookupException("Cannot lookup an object using a null accession");
        }

        log.debug(handlerName + " doing lookup for experiment " + accession);
        log.trace("Thread [" + Thread.currentThread().getName() + "] polling for dependent object");
        // fetch from the bag
        while (cache.fetchExperiment(accession) == null &&
                investigation.getStatus() != Status.COMPLETE &&
                investigation.getStatus() != Status.FAILED) {
            // object isn't in the bag yet, so wait
            synchronized (cache) {
                try {
                    log.trace("Thread [" + Thread.currentThread().getName() + "] waiting, no result yet");
                    // wait for new objects to be available
                    cache.wait(1000);
                    log.trace("Thread [" + Thread.currentThread().getName() + "] resumed");
                }
                catch (InterruptedException e) {
                    if (investigation.getStatus() == Status.FAILED) {
                        log.warn(handlerName + " was interrupted by a failure elsewhere " +
                                "whilst waiting for experiment " + accession + " and is terminating");
                        throw new LookupException(
                                "Interrupted by a fail whilst waiting " + "for experiment " + accession);
                    }
                    else {
                        // interrupted but no fail, so safe to continue
                    }
                }
            }
        }
        log.debug(handlerName + " resumed after dependent object obtained");
        return cache.fetchExperiment(accession);
    }

    /**
     * Blocking method that waits until an assay with the given accession number is available.  Note that this method
     * will be interrupted if the investigation acquires a {@link uk.ac.ebi.arrayexpress2.magetab.datamodel.Status}<code>.FAILED</code>
     * status, but not otherwise.  If the calling code never writes an assay with this accession into a cache associated
     * with the given investigation, this method will never terminate.
     *
     * @param accession     the accession of the assay to wait for
     * @param investigation the investigation to wait on.  This investigation is used to retrieve an {@link
     *                      uk.ac.ebi.gxa.loader.cache.AtlasLoadCache} from the {@link uk.ac.ebi.gxa.loader.cache.AtlasLoadCacheRegistry}
     *                      singleton.
     * @param handlerName   the name of the handler (or the client class) that is waiting - this is used for logging
     * @param log           a log stream to write - this is used in debug modes
     * @return true when the IDF has finished compiling, or false if it failed
     * @throws LookupException if lookup failed - for example, if the accession supplied was null, or if the
     *                         investigation acquires a failed status while this method was waiting.
     */
    public static Assay waitForAssay(String accession,
                                     final MAGETABInvestigation investigation,
                                     String handlerName,
                                     Log log) throws LookupException {
        // retrieve object bag
        AtlasLoadCache cache = AtlasLoadCacheRegistry.getRegistry().retrieveAtlasLoadCache(investigation);

        // check the identifier is not null
        if (accession == null) {
            throw new LookupException("Cannot lookup an object using a null accession");
        }

        log.debug(handlerName + " doing lookup for assay " + accession + " in " + cache.toString());
        log.trace("Thread [" + Thread.currentThread().getName() + "] polling for assay");
        // fetch from the bag
        while (cache.fetchAssay(accession) == null &&
                investigation.getStatus() != Status.COMPLETE &&
                investigation.getStatus() != Status.FAILED) {
            // object isn't in the bag yet, so wait
            synchronized (cache) {
                try {
                    log.trace("Thread [" + Thread.currentThread().getName() + "] waiting, no result yet");
                    // wait for new objects to be available
                    cache.wait(1000);
                    log.trace("Thread [" + Thread.currentThread().getName() + "] resumed");
                }
                catch (InterruptedException e) {
                    if (investigation.getStatus() == Status.FAILED) {
                        log.warn(handlerName + " was interrupted by a failure elsewhere " +
                                "whilst waiting for assay " + accession + " and is terminating");
                        throw new LookupException(
                                "Interrupted by a fail whilst waiting for assay " + accession);
                    }
                    else {
                        // interrupted but no fail, so safe to continue
                    }
                }
            }
        }
        log.debug(handlerName + " resumed after dependent object obtained");
        return cache.fetchAssay(accession);
    }

    /**
     * Blocking method that waits until an sample with the given accession number is available.  Note that this method
     * will be interrupted if the investigation acquires a {@link uk.ac.ebi.arrayexpress2.magetab.datamodel.Status}<code>.FAILED</code>
     * status, but not otherwise.  If the calling code never writes an sample with this accession into a cache
     * associated with the given investigation, this method will never terminate.
     *
     * @param accession     the accession of the sample to wait for
     * @param investigation the investigation to wait on.  This investigation is used to retrieve an {@link
     *                      uk.ac.ebi.gxa.loader.cache.AtlasLoadCache} from the {@link uk.ac.ebi.gxa.loader.cache.AtlasLoadCacheRegistry}
     *                      singleton.
     * @param handlerName   the name of the handler (or the client class) that is waiting - this is used for logging
     * @param log           a log stream to write - this is used in debug modes
     * @return true when the IDF has finished compiling, or false if it failed
     * @throws LookupException if lookup failed - for example, if the accession supplied was null, or if the
     *                         investigation acquires a failed status while this method was waiting.
     */
    public static Sample waitForSample(String accession,
                                       final MAGETABInvestigation investigation,
                                       String handlerName,
                                       Log log) throws LookupException {
        // retrieve object bag
        AtlasLoadCache cache = AtlasLoadCacheRegistry.getRegistry().retrieveAtlasLoadCache(investigation);

        // check the identifier is not null
        if (accession == null) {
            throw new LookupException("Cannot lookup an object using a null accession");
        }

        log.debug(handlerName + " doing lookup for sample " + accession);
        log.trace("Thread [" + Thread.currentThread().getName() + "] polling for sample");
        // fetch from the bag
        while (cache.fetchSample(accession) == null &&
                investigation.getStatus() != Status.COMPLETE &&
                investigation.getStatus() != Status.FAILED) {
            // object isn't in the bag yet, so wait
            synchronized (cache) {
                try {
                    log.trace("Thread [" + Thread.currentThread().getName() + "] waiting, no result yet");
                    // wait for new objects to be available
                    cache.wait(1000);
                    log.trace("Thread [" + Thread.currentThread().getName() + "] resumed");
                }
                catch (InterruptedException e) {
                    if (investigation.getStatus() == Status.FAILED) {
                        log.warn(handlerName + " was interrupted by a failure elsewhere " +
                                "whilst waiting for sample " + accession + " and is terminating");
                        throw new LookupException("Interrupted by a fail whilst waiting for sample " + accession);
                    }
                    else {
                        // interrupted but no fail, so safe to continue
                    }
                }
            }
        }
        log.debug(handlerName + " resumed after dependent object obtained");
        return cache.fetchSample(accession);
    }

    /**
     * Generates an accession number for the given {@link SDRFNode} in the given {@link
     * uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation}. This uses a simple concatenation of
     * investigation accession, followed by "::", followed by the node type, then "::" then the node name.
     *
     * @param investigation the investigation this node is present in.  If this investigation has no accession number,
     *                      "UNKNOWN" is used as the first part of the resulting accession
     * @param node          the node to generate the accession for
     * @return the accession that was generated
     */
    public static String getNodeAccession(MAGETABInvestigation investigation, SDRFNode node) {
        // no requirement to be unique, so just return node name
        return node.getNodeName();
    }
}
