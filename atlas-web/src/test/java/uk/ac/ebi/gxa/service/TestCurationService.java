package uk.ac.ebi.gxa.service;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.gxa.dao.AtlasDAOTestCase;
import uk.ac.ebi.gxa.exceptions.ResourceNotFoundException;
import uk.ac.ebi.microarray.atlas.api.ApiOntology;
import uk.ac.ebi.microarray.atlas.api.ApiOntologyTerm;
import uk.ac.ebi.microarray.atlas.api.ApiProperty;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;

/**
 * @author Robert Petryszak
 */
public class TestCurationService extends AtlasDAOTestCase {

    private static final String CELL_TYPE = "cell_type";
    private static final String PROP3 = "prop3";
    private static final String VALUE007 = "value007";
    private static final String VALUE004 = "value004";
    private static final String VALUE010 = "value010";
    private static final String MICROGLIAL_CELL = "microglial cell";
    private static final String E_MEXP_420 = "E-MEXP-420";
    private static final String ASSAY_ACC = "abc:ABCxyz:SomeThing:1234.ABC123";
    private static final String SAMPLE_ACC = "abc:some/Sample:ABC_DEFG_123a";
    private static final String EFO = "EFO";
    private static final String VBO = "VBO";
    private static final String EFO_0000827 = "EFO_0000827";
    private static final String EFO_0000828 = "EFO_0000828";
    private static final String VBO_0000001 = "VBO_0000001";

    private static final Function<ApiProperty, String> PROPERTY_NAME_FUNC1 =
            new Function<ApiProperty, String>() {
                @Override
                public String apply(@Nonnull ApiProperty input) {
                    return input.getName();
                }
            };

    private static final Function<ApiProperty, String> PROPERTY_VALUE_FUNC1 =
            new Function<ApiProperty, String>() {
                @Override
                public String apply(@Nonnull ApiProperty input) {
                    return input.getValue();
                }
            };

    @Autowired
    private CurationService curationService;

    @Test
    public void testGetProperties() throws Exception {
        List<String> propertyNames = curationService.getPropertyNames();
        assertTrue("No property names found", propertyNames.size() > 0);
        assertTrue("Property name: " + CELL_TYPE + " not found", propertyNames.contains(CELL_TYPE));
    }

    @Test
    public void testGetPropertyValues() throws Exception {
        assertTrue("Property name: " + CELL_TYPE + " does not exist", curationService.getPropertyNames().contains(CELL_TYPE));
        Collection<String> propertyValues = curationService.getPropertyValues(CELL_TYPE);
        assertTrue("No property values found", propertyValues.size() > 0);
        assertTrue("Property value: " + VALUE007 + " not found", propertyValues.contains(VALUE007));
    }

    @Test
    public void testGetExperiment() throws Exception {
        try {
            curationService.getExperiment(E_MEXP_420);
        } catch (ResourceNotFoundException e) {
            fail("Experiment: " + E_MEXP_420 + " not found");
        }
    }

    @Test
    public void testReplacePropertyValueInAssays() throws Exception {
        // Test replace VALUE007 (already a property of ASSAY_ACC) with VALUE010 (not a property of ASSAY_ACC)
        Collection<ApiProperty> assayProperties = curationService.getAssayProperties(E_MEXP_420, ASSAY_ACC);

        assertTrue("Property : " + CELL_TYPE + ":" + VALUE007 + " not found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, VALUE007));
        assertFalse("Property : " + CELL_TYPE + ":" + VALUE010 + " found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, VALUE010));

        curationService.replacePropertyValueInAssays(CELL_TYPE, VALUE007, VALUE010);

        assertFalse("Property : " + CELL_TYPE + ":" + VALUE007 + " found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, VALUE007));
        assertTrue("Property : " + CELL_TYPE + ":" + VALUE010 + " not found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, VALUE010));
    }

    @Test
    public void testReplacePropertyValueInAssays1() throws Exception {
        // Test replace MICROGLIAL_CELL with VALUE004 (both properties of ASSAY_ACC)
        Collection<ApiProperty> assayProperties = curationService.getAssayProperties(E_MEXP_420, ASSAY_ACC);

        assertTrue("Property : " + CELL_TYPE + ":" + MICROGLIAL_CELL + " not found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, MICROGLIAL_CELL));

        // First add VALUE004 to ASSAY_ACC properties
        Set<ApiOntologyTerm> terms = Sets.newHashSet();
        terms.add(curationService.getOntologyTerm(EFO_0000828));
        ApiProperty apiProperty = new ApiProperty(CELL_TYPE, VALUE004, terms);
        ApiProperty[] newProps = new ApiProperty[1];
        newProps[0] = apiProperty;
        curationService.putAssayProperties(E_MEXP_420, ASSAY_ACC, newProps);

        // Now that both MICROGLIAL_CELL and VALUE004 are both present in ASSAY_ACC, replace MICROGLIAL_CELL with VALUE004
        curationService.replacePropertyValueInAssays(CELL_TYPE, MICROGLIAL_CELL, VALUE004);

        assertFalse("Property : " + CELL_TYPE + ":" + MICROGLIAL_CELL + " found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, MICROGLIAL_CELL));
        assertTrue("Property : " + CELL_TYPE + ":" + VALUE004 + " not found in assay properties",
                propertyPresent(assayProperties, CELL_TYPE, VALUE004));

        for (ApiProperty property : assayProperties) {
            if (CELL_TYPE.equals(property.getName()) && VALUE004.equals(property.getValue())) {
                Set<ApiOntologyTerm> newTerms = property.getTerms();
                assertEquals(2, newTerms.size());
                // Set of terms in the retained VALUE004 property should be a superset of terms assigned
                // to the replaced VALUE010 and to the replacing VALUE004
                assertTrue(newTerms + " doesn't contain " + curationService.getOntologyTerm(EFO_0000827),
                        newTerms.contains(curationService.getOntologyTerm(EFO_0000827))); // from property VALUE010
                assertTrue(newTerms + " doesn't contain " + curationService.getOntologyTerm(EFO_0000828),
                        newTerms.contains(curationService.getOntologyTerm(EFO_0000828))); // from property VALUE004
            }
        }
    }

    @Test
    public void testReplacePropertyValueInSamples() throws Exception {
        // Test replace VALUE004 (already a property of SAMPLE_ACC) with VALUE010 (not a property of SAMPLE_ACC)
        Collection<ApiProperty> sampleProperties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);

        assertTrue("Property : " + PROP3 + ":" + VALUE004 + " not found in sample properties",
                propertyPresent(sampleProperties, PROP3, VALUE004));
        assertFalse("Property : " + PROP3 + ":" + VALUE010 + " found in sample properties",
                propertyPresent(sampleProperties, PROP3, VALUE010));

        curationService.replacePropertyValueInSamples(PROP3, VALUE004, VALUE010);

        assertFalse("Property : " + PROP3 + ":" + VALUE004 + " found in sample properties",
                propertyPresent(sampleProperties, PROP3, VALUE004));
        assertTrue("Property : " + PROP3 + ":" + VALUE010 + " not found in sample properties",
                propertyPresent(sampleProperties, PROP3, VALUE010));
    }

    @Test
    public void testReplacePropertyValueInSamples1() throws Exception {
        // Test replace VALUE004 with VALUE010 (both properties of SAMPLE_ACC)
        Collection<ApiProperty> sampleProperties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);

        assertTrue("Property : " + PROP3 + ":" + VALUE004 + " not found in sample properties",
                propertyPresent(sampleProperties, PROP3, VALUE004));

        // First add VALUE010 to SAMPLE_ACC properties
        Set<ApiOntologyTerm> terms = Sets.newHashSet();
        terms.add(curationService.getOntologyTerm(EFO_0000828));
        ApiProperty apiProperty = new ApiProperty(PROP3, VALUE010, terms);
        ApiProperty[] newProps = new ApiProperty[1];
        newProps[0] = apiProperty;
        curationService.putSampleProperties(E_MEXP_420, SAMPLE_ACC, newProps);

        // Now that both MICROGLIAL_CELL and VALUE004 are both present in ASSAY_ACC, replace VALUE004 with VALUE010
        curationService.replacePropertyValueInSamples(PROP3, VALUE004, VALUE010);

        assertFalse("Property : " + PROP3 + ":" + VALUE004 + " found in assay properties",
                propertyPresent(sampleProperties, PROP3, VALUE004));
        assertTrue("Property : " + PROP3 + ":" + VALUE010 + " not found in assay properties",
                propertyPresent(sampleProperties, PROP3, VALUE010));

        for (ApiProperty property : sampleProperties) {
            if (PROP3.equals(property.getName()) && VALUE010.equals(property.getValue())) {
                Set<ApiOntologyTerm> newTerms = property.getTerms();
                assertEquals(1, newTerms.size());
                // Set of terms in the retained VALUE004 property should be a superset of terms assigned
                // to the replaced VALUE010 and to the replacing VALUE004
                assertTrue(newTerms + " doesn't contain " + curationService.getOntologyTerm(EFO_0000828),
                        newTerms.contains(curationService.getOntologyTerm(EFO_0000828))); // from property VALUE010
            }
        }
    }


    @Test
    public void testRemovePropertyValue() throws Exception {
        prepareRemoval();

        validateRemoval();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validateRemoval() throws ResourceNotFoundException {
        assertFalse("Property : " + CELL_TYPE + ":" + VALUE007 + " not removed from assay properties",
                assayPropertyPresent(E_MEXP_420, ASSAY_ACC, CELL_TYPE, VALUE007));
        assertFalse("Property : " + PROP3 + ":" + VALUE004 + " not removed from sample properties",
                samplePropertyPresent(E_MEXP_420, SAMPLE_ACC, PROP3, VALUE004));

        Collection<String> propertyValues = curationService.getPropertyValues(CELL_TYPE);
        assertFalse("Property value: " + VALUE007 + " found", propertyValues.contains(VALUE007));

        propertyValues = curationService.getPropertyValues(PROP3);
        assertFalse("Property value: " + VALUE004 + " found", propertyValues.contains(VALUE004));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void prepareRemoval() throws ResourceNotFoundException {
        assertTrue("Property : " + CELL_TYPE + ":" + VALUE007 + " not found in assay properties",
                assayPropertyPresent(E_MEXP_420, ASSAY_ACC, CELL_TYPE, VALUE007));
        assertTrue("Property : " + PROP3 + ":" + VALUE004 + " not found in sample properties",
                samplePropertyPresent(E_MEXP_420, SAMPLE_ACC, PROP3, VALUE004));

        curationService.removePropertyValue(CELL_TYPE, VALUE007);
        curationService.removePropertyValue(PROP3, VALUE004);
    }

    @Test
    public void testGetAssay() throws Exception {
        try {
            curationService.getAssay(E_MEXP_420, ASSAY_ACC);
        } catch (ResourceNotFoundException e) {
            fail("Assay: " + ASSAY_ACC + " in experiment: " + E_MEXP_420 + " not found");
        }
    }

    @Test
    public void testGetSample() throws Exception {
        try {
            curationService.getSample(E_MEXP_420, SAMPLE_ACC);
        } catch (ResourceNotFoundException e) {
            fail("Sample: " + SAMPLE_ACC + " in experiment: " + E_MEXP_420 + " not found");
        }
    }

    @Test
    public void testGetAssayProperties() throws Exception {
        Collection<ApiProperty> properties = curationService.getAssayProperties(E_MEXP_420, ASSAY_ACC);
        assertTrue("No properties found in assay: " + ASSAY_ACC + " in experiment: " + E_MEXP_420, properties.size() > 0);
        assertTrue("Assay property name: " + CELL_TYPE + " not found", transform(properties, PROPERTY_NAME_FUNC1).contains(CELL_TYPE));
        assertTrue("Assay property value: " + VALUE007 + " not found", transform(properties, PROPERTY_VALUE_FUNC1).contains(VALUE007));
    }

    @Test
    public void testAddDeleteAssayProperties() throws Exception {
        Collection<ApiProperty> properties = curationService.getAssayProperties(E_MEXP_420, ASSAY_ACC);
        assertTrue("No properties found in sample: " + SAMPLE_ACC + " in experiment: " + E_MEXP_420, properties.size() > 0);

        ApiProperty apiProperty = properties.iterator().next();
        apiProperty.update(PROP3, VALUE004);
        ApiProperty[] newProps = {apiProperty};

        curationService.deleteAssayProperties(E_MEXP_420, ASSAY_ACC, newProps);
        assertFalse("Property : " + PROP3 + ":" + VALUE004 + " not deleted in assay properties", assayPropertyPresent(E_MEXP_420, ASSAY_ACC, PROP3, VALUE004));

        curationService.putAssayProperties(E_MEXP_420, ASSAY_ACC, newProps);
        assertTrue("Property : " + PROP3 + ":" + VALUE004 + " not added to assay properties", assayPropertyPresent(E_MEXP_420, ASSAY_ACC, PROP3, VALUE004));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean assayPropertyPresent(String experiment, String assay, String property, String value) throws ResourceNotFoundException {
        return propertyPresent(curationService.getAssayProperties(experiment, assay), property, value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean samplePropertyPresent(String experiment, String sample, String property, String value) throws ResourceNotFoundException {
        return propertyPresent(curationService.getSampleProperties(experiment, sample), property, value);
    }

    @Test
    public void testGetSampleProperties() throws Exception {
        Collection<ApiProperty> properties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);
        assertTrue("No properties found in sample: " + SAMPLE_ACC + " in experiment: " + E_MEXP_420, properties.size() > 0);
        assertTrue("Sample property name: " + PROP3 + " not found", transform(properties, PROPERTY_NAME_FUNC1).contains(PROP3));
        assertTrue("Sample property value: " + VALUE004 + " not found", transform(properties, PROPERTY_VALUE_FUNC1).contains(VALUE004));
    }

    @Test
    public void testAddDeleteSampleProperties() throws Exception {
        Collection<ApiProperty> properties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);
        assertTrue("No properties found in sample: " + SAMPLE_ACC + " in experiment: " + E_MEXP_420, properties.size() > 0);

        ApiProperty apiProperty = properties.iterator().next();
        apiProperty.update(PROP3, VALUE004);
        ApiProperty[] newProps = new ApiProperty[1];
        newProps[0] = apiProperty;

        curationService.deleteSampleProperties(E_MEXP_420, SAMPLE_ACC, newProps);
        properties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);
        assertFalse("Property : " + PROP3 + ":" + VALUE004 + " not deleted in sample properties", propertyPresent(properties, PROP3, VALUE004));

        curationService.putSampleProperties(E_MEXP_420, SAMPLE_ACC, newProps);
        properties = curationService.getSampleProperties(E_MEXP_420, SAMPLE_ACC);
        assertTrue("Property : " + PROP3 + ":" + VALUE004 + " not added to sample properties", propertyPresent(properties, PROP3, VALUE004));
    }

    @Test
    public void testGetOntology() throws Exception {
        ApiOntology ontology = curationService.getOntology(EFO);
        assertNotNull("Ontology: " + EFO + " not found ", ontology);
    }

    @Test
    public void testPutOntology() throws Exception {
        ApiOntology ontology = curationService.getOntology(EFO);
        ontology.setName(VBO);
        try {
            curationService.getOntology(VBO);
            fail("Ontology: " + VBO + " already exists");
        } catch (ResourceNotFoundException e) {

        }

        // Create new ontology
        curationService.putOntology(ontology);
        try {
            curationService.getOntology(VBO);
        } catch (ResourceNotFoundException e) {
            fail("Failed to create ontology: " + VBO);
        }

        // Update ontology
        String newDescr = "Updated " + ontology.getDescription();
        ontology.setDescription(newDescr);
        curationService.putOntology(ontology);
        try {
            ApiOntology updatedOntology = curationService.getOntology(VBO);
            assertEquals(newDescr, updatedOntology.getDescription());
        } catch (ResourceNotFoundException e) {
            fail("Failed to create ontology: " + VBO);
        }

    }

    @Test
    public void testGetOntologyTerm() throws Exception {
        ApiOntologyTerm ontologyTerm = curationService.getOntologyTerm(EFO_0000827);
        assertNotNull("Ontology term: " + EFO_0000827 + " not found ", ontologyTerm);
    }

    @Test
    public void testPutOntologyTerms() throws Exception {
        ApiOntologyTerm ontologyTerm = curationService.getOntologyTerm(EFO_0000827);

        try {
            curationService.getOntologyTerm(VBO_0000001);
            fail("Ontology term: " + VBO_0000001 + " already exists");
        } catch (ResourceNotFoundException e) {

        }

        // Create new ontology term
        ontologyTerm.setAccession(VBO_0000001);

        ApiOntologyTerm[] ontologyTerms = new ApiOntologyTerm[1];
        ontologyTerms[0] = ontologyTerm;
        curationService.putOntologyTerms(ontologyTerms);

        try {
            curationService.getOntologyTerm(VBO_0000001);
        } catch (ResourceNotFoundException e) {
            fail("Failed to create ontology term: " + VBO_0000001);
        }

        // Update ontology term
        String newDescr = "Updated " + ontologyTerm.getDescription();
        ontologyTerm.setDescription(newDescr);
        curationService.putOntologyTerms(ontologyTerms);

        try {
            ApiOntologyTerm updatedOntologyTerm = curationService.getOntologyTerm(VBO_0000001);
            assertEquals(newDescr, updatedOntologyTerm.getDescription());
        } catch (ResourceNotFoundException e) {
            fail("Failed to create ontology term: " + VBO_0000001);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean propertyPresent(Collection<ApiProperty> properties, String propertyName, String propertyValue) {
        boolean found = false;
        for (ApiProperty property : properties) {
            if (propertyName.equals(property.getName()) && propertyValue.equals(property.getValue()))
                found = true;
        }
        return found;
    }
}
