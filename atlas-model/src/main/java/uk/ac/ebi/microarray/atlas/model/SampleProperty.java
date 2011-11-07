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

package uk.ac.ebi.microarray.atlas.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Entity
@Table(name = "A2_SAMPLEPV")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public final class SampleProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "samplePVSeq")
    @SequenceGenerator(name = "samplePVSeq", sequenceName = "A2_SAMPLEPV_SEQ", allocationSize = 1)
    private Long samplepvid;
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    private Sample sample;
    @Nonnull
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    private PropertyName propertyName;
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    private PropertyValue propertyValue;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    // TODO: 4alf: this can be expressed in NamingStrategy
    @JoinTable(name = "A2_SAMPLEPVONTOLOGY",
            joinColumns = @JoinColumn(name = "SAMPLEPVID", referencedColumnName = "SAMPLEPVID"),
            inverseJoinColumns = @JoinColumn(name = "ONTOLOGYTERMID", referencedColumnName = "ONTOLOGYTERMID"))
    @Fetch(FetchMode.SUBSELECT)
    private List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

    SampleProperty() {
    }

    public SampleProperty(Sample sample, Property p) {
        this(sample, p, Collections.<OntologyTerm>emptyList());
    }

    public SampleProperty(Sample sample, Property p, Collection<OntologyTerm> efoTerms) {
        this.sample = sample;
        propertyName = p.name();
        propertyValue = p.value();
        terms.addAll(efoTerms);
    }

    public Long getId() {
        return samplepvid;
    }

    public String getName() {
        return propertyName.getName();
    }

    public String getValue() {
        return propertyValue.getValue();
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public List<OntologyTerm> getTerms() {
        return unmodifiableList(terms);
    }

    @Override
    public String toString() {
        return "SampleProperty{" +
                "propertyValue=" + propertyValue +
                ", terms='" + terms + '\'' +
                '}';
    }

    public void setTerms(List<OntologyTerm> terms) {
        this.terms = terms;
    }

    public PropertyName getDefinition() {
        return propertyName;
    }

    public boolean is(Property property) {
        return propertyName.equals(property.name()) && propertyValue.equals(propertyValue);
    }
}
