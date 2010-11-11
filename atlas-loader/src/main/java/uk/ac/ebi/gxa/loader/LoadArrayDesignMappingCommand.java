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

package uk.ac.ebi.gxa.loader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Load bioentities command has URL and boolean to indicate either corresponding virtual arraydesign needs to be loaded/updated
 */
public class LoadArrayDesignMappingCommand extends AbstractURLCommand {

    //ToDo: pass in a command already processed properties
    private String adAccMappingFile = "/Users/nsklyar/Data/annotations/ad_acc_mapping.txt";

    public LoadArrayDesignMappingCommand(URL url) {
        super(url);
    }

    public LoadArrayDesignMappingCommand(String url) throws MalformedURLException {
        super(url);
    }

    public void visit(AtlasLoaderCommandVisitor visitor) throws AtlasLoaderException {
        visitor.process(this);
    }

    public LoadArrayDesignMappingCommand(URL url, String adAccMappingFile) {
        super(url);
        this.adAccMappingFile = adAccMappingFile;
    }

    public LoadArrayDesignMappingCommand(String url, String adAccMappingFile) throws MalformedURLException {
        super(url);
        this.adAccMappingFile = adAccMappingFile;
    }

    public String getAdAccMappingFile() {
        return adAccMappingFile;
    }

    @Override
    public String toString() {
        return "Load array design mappings from " + getUrl();
    }
}