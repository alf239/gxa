/*
 * Copyright 2008-2011 Microarray Informatics Team, EMBL-European Bioinformatics Institute
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

package uk.ac.ebi.microarray.atlas.model.bioentity;

public class BEPropertyValue {
    private Long bepropertyvalueid;
    private String value;
    private BioEntityProperty property;


    BEPropertyValue() {
    }

    public BEPropertyValue(Long bepropertyvalueid, BioEntityProperty property, String value) {
        this.bepropertyvalueid = bepropertyvalueid;
        this.value = value;
        this.property = property;
    }

    public BEPropertyValue(BioEntityProperty property, String value) {
        this.value = value;
        this.property = property;
    }

    public Long getId() {
        return bepropertyvalueid;
    }

    public BioEntityProperty getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BEPropertyValue that = (BEPropertyValue) o;

        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }
}
