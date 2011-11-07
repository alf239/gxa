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

package uk.ac.ebi.microarray.atlas.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * @author alf
 */
@Immutable
public class Property {
    @Nonnull
    private final PropertyName name;
    @Nonnull
    private final PropertyValue value;

    public Property(PropertyName name, PropertyValue value) {
        if (name == null)
            throw new NullPointerException("name is null");
        if (value == null)
            throw new NullPointerException("value is null");
        this.name = name;
        this.value = value;
    }

    @Nonnull
    public String getName() {
        return name.getName();
    }

    @Nonnull
    public String getDisplayName() {
        return name.getDisplayName();
    }

    @Nonnull
    public String getValue() {
        return value.getValue();
    }

    @Nonnull
    PropertyName name() {
        return name;
    }

    @Nonnull
    PropertyValue value() {
        return value;
    }

    @Nonnull
    public String uniqueKey() {
        return name.getId() + ":" + value.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (!name.equals(property.name)) return false;
        if (!value.equals(property.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
