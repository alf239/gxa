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

package uk.ac.ebi.gxa.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.rcloud.server.DirectJNI;
import uk.ac.ebi.rcloud.server.RServices;

import java.io.File;

/**
 * A concrete implementation of {@link uk.ac.ebi.gxa.R.AtlasRFactory} that generates RServices that run on the local
 * machine.  As local R installations are not thread-safe, only one computation can be calculated at a time.  Therefore,
 * createRServices() will release at most one RService for use at any one time, and repeat requests will block until the
 * previously acquired RService has been released.
 *
 * @author Tony Burdett
 * @date 17-Nov-2009
 */
public class LocalAtlasRFactory implements AtlasRFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());


    public boolean validateEnvironment() throws AtlasRServicesException {
        // check environment, system properties
        String r_home = null;
        if ((System.getenv("R_HOME") == null || System.getenv("R_HOME").equals("")) &&
                (System.getProperty("R_HOME") == null || System.getProperty("R_HOME").equals(""))) {
            log.error("No $R_HOME property set - this is required to start JNI bridge to R");
            return false;
        }
        else {
            r_home = System.getenv("R_HOME");
            if (r_home == null || r_home.equals("")) {
                r_home = System.getProperty("R_HOME");
            }
        }

        // r_home definitely not null or "" now
        if (r_home == null || r_home.equals("")) {
            log.error("$R_HOME is empty");
            return false;
        }
        else {
            // append r_home to java.library.path, this should allow discovery of required JNI/rJava lib files
            String append = ":" + r_home + File.separator + "bin:" + r_home + File.separator + "lib";
            System.setProperty("java.library.path", System.getProperty("java.library.path") + append);
        }

        // check R install actually works
        try {
            RServices r = createRServices();
            recycleRServices(r);
        }
        catch (Throwable e) {
            log.error("Critical R whilst trying to bridge to local R install - " +
                    "check R is installed and required libraries present", e);
            return false;
        }

        // checks passed so return true
        return true;
    }

    public RServices createRServices() throws AtlasRServicesException {
        // create a R service - DirectJNI gets an R service on the local machine
        return DirectJNI.getInstance().getRServices();
    }

    public void recycleRServices(RServices rServices) {
    }

    public void releaseResources() {
        try {
            DirectJNI.getInstance()._rEngine.end();
//            DirectJNI.getInstance()._rEngine.eval("quit('no')");
        } catch(Throwable t) {
            log.error("Error", t);
        }
    }
}
