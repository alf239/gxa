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

package uk.ac.ebi.gxa.model.impl;

import uk.ac.ebi.gxa.model.Sample;
import uk.ac.ebi.gxa.model.PropertyCollection;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Andrey
 * Date: Nov 26, 2009
 * Time: 11:31:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class AtlasSample implements Sample {

    private String accession;
    private int id;
    private Collection<String> assayAccessions;
    private PropertyCollection properties;

    public String getAccession(){
        return accession;
    }
    public void setAccession(String accession){
        this.accession = accession;
    }

    public int getId(){
        return id;
    }
    public void setid(int id){
        this.id = id;
    }

    public Collection<String> getAssayAccessions(){
        return this.assayAccessions;
    }
    public void setAssayAccessions(Collection<String> assayAccessions){
        this.assayAccessions = assayAccessions;
    }

    public PropertyCollection getProperties(){
        return this.properties;
    }
    public void setProperties(PropertyCollection properties){
        this.properties = properties;
    }

    public Collection<String> getExperimentAccessions(){
        return null;
    }
}
