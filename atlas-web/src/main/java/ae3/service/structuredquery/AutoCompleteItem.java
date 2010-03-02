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

package ae3.service.structuredquery;

/**
 * @author pashky
 */
public class AutoCompleteItem implements Comparable<AutoCompleteItem> {
    private final String property;
    private final String value;
    private final Long count;
    private final String id;

    public AutoCompleteItem(String property, final String id, String value, Long count) {
        this.property = property;
        this.value = value;
        this.count = count;
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public Long getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public int compareTo(AutoCompleteItem o) {
        return value.toLowerCase().compareTo(o.getValue().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutoCompleteItem that = (AutoCompleteItem) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
