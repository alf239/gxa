package uk.ac.ebi.microarray.atlas.api;

import uk.ac.ebi.microarray.atlas.model.AssayProperty;
import uk.ac.ebi.microarray.atlas.model.OntologyTerm;
import uk.ac.ebi.microarray.atlas.model.SampleProperty;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.gxa.utils.TransformerUtil.instanceTransformer;

/**
 * Class to represent API representations of Sample or Assay properties
 *
 * @author Misha Kapushesky
 */
public class ApiProperty {
    private String name;
    private String value;
    private Set<ApiOntologyTerm> terms;

    public ApiProperty() {
    }

    public ApiProperty(String name, String value, final Set<ApiOntologyTerm> terms) {
        this.name = name;
        this.value = value;
        this.terms = terms;
    }

    public ApiProperty(final AssayProperty assayProperty) {
        this(assayProperty.getName(), assayProperty.getValue(), extractTerms(assayProperty.getTerms()));
    }

    public ApiProperty(final SampleProperty sampleProperty) {
        this(sampleProperty.getName(), sampleProperty.getValue(), extractTerms(sampleProperty.getTerms()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void update(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Set<ApiOntologyTerm> getTerms() {
        return terms;
    }

    public void setTerms(Set<ApiOntologyTerm> terms) {
        this.terms = terms;
    }

    private static Set<ApiOntologyTerm> extractTerms(List<OntologyTerm> terms) {
        return newHashSet(transform(terms, instanceTransformer(OntologyTerm.class, ApiOntologyTerm.class)));
    }
}
