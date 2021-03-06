package uk.ac.ebi.microarray.atlas.api;

import uk.ac.ebi.microarray.atlas.model.AssayProperty;
import uk.ac.ebi.microarray.atlas.model.OntologyTerm;
import uk.ac.ebi.microarray.atlas.model.SampleProperty;

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
    private ApiPropertyValue propertyValue;
    private Set<ApiOntologyTerm> terms;

    public ApiProperty() {
    }

    public ApiProperty(final ApiPropertyValue apiPropertyValue, final Set<ApiOntologyTerm> terms) {
        this.propertyValue = apiPropertyValue;
        this.terms = terms;
    }

    public ApiProperty(final AssayProperty assayProperty) {
        this.propertyValue = new ApiPropertyValue(assayProperty.getPropertyValue());

        this.terms = newHashSet(
                transform(assayProperty.getTerms(),
                        instanceTransformer(OntologyTerm.class, ApiOntologyTerm.class)));
    }

    public ApiProperty(final SampleProperty assayProperty) {
        this.propertyValue = new ApiPropertyValue(assayProperty.getPropertyValue());

        this.terms = newHashSet(
                transform(assayProperty.getTerms(),
                        instanceTransformer(OntologyTerm.class, ApiOntologyTerm.class)));
    }

    public ApiPropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(ApiPropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Set<ApiOntologyTerm> getTerms() {
        return terms;
    }

    public void setTerms(Set<ApiOntologyTerm> terms) {
        this.terms = terms;
    }
}
