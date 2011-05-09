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

package uk.ac.ebi.gxa.loader.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton registry of AtlasLoadCache objects, indexed by MAGETABInvestigation.  This lets handlers in different
 * threads do a lookup on the cache to store objects in, and they only need to specifically know about the
 * MAGETABInvestigation.
 *
 * @author Tony Burdett
 */
public class AtlasLoadCacheRegistry {
    // singleton instance
    private static AtlasLoadCacheRegistry registry = new AtlasLoadCacheRegistry();

    /**
     * Obtain the singleton registry instance
     *
     * @return the AtlasLoadCache registry
     */
    public static AtlasLoadCacheRegistry getRegistry() {
        return registry;
    }

    private final Map<MAGETABInvestigation, AtlasLoadCache> investigationRegistry;
    private final Log log = LogFactory.getLog(this.getClass().getSimpleName());

    /**
     * Private constructor for the registry
     */
    private AtlasLoadCacheRegistry() {
        this.investigationRegistry = new HashMap<MAGETABInvestigation, AtlasLoadCache>();
    }

    /**
     * Register an {@link AtlasLoadCache}, keyed by investigation, to this registry.  Any objects created from this
     * investigation should be placed into this cache.
     * <p/>
     * Note that an IllegalArgumentException will be thrown if the investigation supplied is already associated with a
     * cache in this registry.  If you want to replace a cache, use the {@link #replaceExperiment(uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation,
     * AtlasLoadCache)} method, or you can {@link #deregisterExperiment(uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation)}
     * and then register.
     *
     * @param investigation the investigation being used to create objects for the cache
     * @param cache         the cache holding objects created from this investigation
     */
    public synchronized void registerExperiment(MAGETABInvestigation investigation,
                                                AtlasLoadCache cache) {
        log.info("Registering cache, and associating with an investigation");
        // register - but only if this investigation hasn't be registered before
        if (investigationRegistry.containsKey(investigation)) {
            throw new IllegalArgumentException(
                    "The supplied investigation has been previously registered");
        }
        else {
            investigationRegistry.put(investigation, cache);
        }
    }

    /**
     * Deregisters the {@link AtlasLoadCache} keyed to this investigation from this registry.
     *
     * @param investigation the investigation that keys the registered cache of objects
     */
    public synchronized void deregisterExperiment(MAGETABInvestigation investigation) {
        log.info("Deregistering cache");

        // now register - but only if this investigation hasn't be registered before
        if (!investigationRegistry.containsKey(investigation)) {
            throw new IllegalArgumentException(
                    "The supplied investigation was never registered");
        }
        else {
            investigationRegistry.remove(investigation);
        }
    }

    /**
     * Replace the {@link AtlasLoadCache} that is currently registered to the given {@link
     * uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation} with the new one supplied.  This is equivalent to
     * calling {@link #deregisterExperiment(uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation)} followed by
     * {@link #registerExperiment(uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation, AtlasLoadCache)} }
     *
     * @param investigation the investigation keying the cache to replace
     * @param cache         the new cache that will replace the current cache
     */
    public synchronized void replaceExperiment(MAGETABInvestigation investigation,
                                               AtlasLoadCache cache) {
        deregisterExperiment(investigation);
        registerExperiment(investigation, cache);
    }

    /**
     * Lookup an {@link AtlasLoadCache} by the investigation being used.
     *
     * @param investigation the investigation being used to create objects
     * @return the cache linked to this investigation
     */
    public synchronized AtlasLoadCache retrieveAtlasLoadCache(
            MAGETABInvestigation investigation) {
        return investigationRegistry.get(investigation);
    }
}
