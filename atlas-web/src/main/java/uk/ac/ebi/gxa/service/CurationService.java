package uk.ac.ebi.gxa.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.gxa.dao.*;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.gxa.exceptions.ResourceNotFoundException;
import uk.ac.ebi.microarray.atlas.api.*;
import uk.ac.ebi.microarray.atlas.model.*;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;

/**
 * This class handles all Curation API requests, delegated from CurationApiController
 *
 * @author Misha Kapushesky
 */
@Service
public class CurationService {
    @Autowired
    private AtlasDAO atlasDAO;

    @Autowired
    private AssayDAO assayDAO;

    @Autowired
    private SampleDAO sampleDAO;

    @Autowired
    private OntologyDAO ontologyDAO;

    @Autowired
    private OntologyTermDAO ontologyTermDAO;

    @Autowired
    private PropertyDAO propertyDAO;

    @Autowired
    private PropertyValueDAO propertyValueDAO;

    private static final Function<PropertyName, String> PROPERTY_NAME =
            new Function<PropertyName, String>() {
                public String apply(@Nonnull PropertyName p) {
                    return p.getName();
                }
            };

    private static final Function<Property, String> PROPERTY_VALUE =
            new Function<Property, String>() {
                public String apply(@Nonnull Property p) {
                    return p.getValue();
                }
            };


    /**
     * @return alphabetically sorted collection of all property names
     */
    public List<String> getPropertyNames() {
        List<PropertyName> propertyName = propertyDAO.getAll();

        List<String> propertyNames = newArrayList(transform(propertyName, PROPERTY_NAME));
        sort(propertyNames);
        return propertyNames;
    }

    /**
     * @param propertyName
     * @return alphabetically sorted collection of values for propertyName
     * @throws ResourceNotFoundException
     */
    @Transactional
    public Collection<String> getPropertyValues(final String propertyName)
            throws ResourceNotFoundException {
        try {
            PropertyName name = propertyDAO.getByName(propertyName);
            List<Property> property = assayDAO.getProperties(name);

            List<String> propertyValues = newArrayList(transform(property, PROPERTY_VALUE));
            sort(propertyValues);
            return propertyValues;
        } catch (RecordNotFoundException e) {
            throw new ResourceNotFoundException("Cannot find property " + propertyName, e);
        }
    }

    /**
     * Remove propertyName:propertyValue from all assays and samples that are mapped to it (via FK cascading in Oracle) and remove propertyValue from
     * the list of values assigned to propertyName
     *
     * @param propertyName
     * @param propertyValue
     * @throws ResourceNotFoundException
     */
    @Transactional
    public void removePropertyValue(final String propertyName,
                                    final String propertyValue) throws ResourceNotFoundException {
        try {
            PropertyName property = propertyDAO.getByName(propertyName);
            PropertyValue propValue = propertyValueDAO.find(propertyValue);

            assayDAO.deleteProperties(property, propValue);
            sampleDAO.deleteProperties(property, propValue);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * Replaces oldValue of property: propertyName with newValue in all assays in which propertyName-oldValue exists.
     * In cases when a given assay contains both oldValue and newValue, the retained newValue gets mapped to the superset of OntologyTerms
     * assigned to oldValue and newValue.
     *
     * @param propertyName
     * @param oldValue
     * @param newValue
     * @throws ResourceNotFoundException if property: propertyName and/or its value: oldValue don't exist
     */
    @Transactional
    public void replacePropertyValueInAssays(
            final String propertyName,
            final String oldValue,
            final String newValue)
            throws ResourceNotFoundException {
        try {
            PropertyName name = propertyDAO.getByName(propertyName);
            final PropertyValue value = propertyValueDAO.find(oldValue);
            Property oldProperty = new Property(name, value);
            Property newProperty = getOrCreateProperty(propertyName, newValue);

            List<Assay> assays = assayDAO.getAssaysByPropertyValue(name, value);
            for (Assay assay : assays) {
                AssayProperty oldAssayProperty = assay.getProperty(oldProperty);
                AssayProperty newAssayProperty = assay.getProperty(newProperty);
                List<OntologyTerm> terms = newArrayList(oldAssayProperty.getTerms());
                assay.deleteProperty(oldProperty);
                if (newAssayProperty != null) {
                    terms.addAll(newAssayProperty.getTerms());
                }
                assay.addOrUpdateProperty(newProperty, terms);
                assayDAO.save(assay);
            }
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * Replaces oldValue of property: propertyName with newValue in all samples in which propertyName-oldValue exists.
     * In cases when a given sample contains both oldValue and newValue, the retained newValue gets mapped to the superset of OntologyTerms
     * assigned to oldValue and newValue.
     *
     * @param propertyName
     * @param oldValue
     * @param newValue
     * @throws ResourceNotFoundException if property: propertyName and/or its value: oldValue don't exist
     */
    @Transactional
    public void replacePropertyValueInSamples(
            final String propertyName,
            final String oldValue,
            final String newValue)
            throws ResourceNotFoundException {
        try {
            PropertyName property = propertyDAO.getByName(propertyName);
            Property oldProperty = new Property(property, propertyValueDAO.find(oldValue));
            Property newProperty = getOrCreateProperty(propertyName, newValue);

            List<Sample> samples = sampleDAO.getSamplesByPropertyValue(oldValue);
            for (Sample sample : samples) {
                SampleProperty oldSampleProperty = sample.getProperty(oldProperty);
                SampleProperty newSampleProperty = sample.getProperty(newProperty);
                List<OntologyTerm> terms = newArrayList(oldSampleProperty.getTerms());
                sample.deleteProperty(oldProperty);
                if (newSampleProperty != null) {
                    terms.addAll(newSampleProperty.getTerms());
                }
                sample.addOrUpdateProperty(newProperty, terms);
                sampleDAO.save(sample);
            }
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * @param experimentAccession
     * @return ApiExperiment corresponding to experimentAccession
     * @throws ResourceNotFoundException if experiment not found
     */
    public ApiExperiment getExperiment(final String experimentAccession)
            throws ResourceNotFoundException {

        try {
            final Experiment experiment = atlasDAO.getExperimentByAccession(experimentAccession);

            return new ApiExperiment(experiment);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * @param experimentAccession
     * @param assayAccession
     * @return ApiAssay corresponding to assayAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or assay: assayAccession in that experiment are not found
     */
    public ApiAssay getAssay(final String experimentAccession, final String assayAccession)
            throws ResourceNotFoundException {

        Assay assay = findAssay(experimentAccession, assayAccession);
        return new ApiAssay(assay);
    }

    /**
     * @param experimentAccession
     * @param sampleAccession
     * @return ApiSample corresponding to sampleAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or sample: sampleAccession in that experiment are not found
     */
    public ApiSample getSample(final String experimentAccession, final String sampleAccession)
            throws ResourceNotFoundException {
        Sample sample = findSample(experimentAccession, sampleAccession);
        return new ApiSample(sample);
    }

    /**
     * @param experimentAccession
     * @param assayAccession
     * @return Collection of ApiAssayProperty for assay: assayAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or assay: assayAccession in that experiment are not found
     */
    @Transactional
    public Collection<ApiProperty> getAssayProperties(
            final String experimentAccession,
            final String assayAccession)
            throws ResourceNotFoundException {
        Assay assay = findAssay(experimentAccession, assayAccession);
        return new ApiAssay(assay).getProperties();
    }

    /**
     * Adds (or updates mapping to efo terms for) assayProperties to assay: assayAccession in experiment: experimentAccession
     *
     * @param experimentAccession
     * @param assayAccession
     * @param assayProperties
     * @throws ResourceNotFoundException if experiment: experimentAccession or assay: assayAccession in that experiment are not found
     */
    @Transactional
    public void putAssayProperties(final String experimentAccession,
                                   final String assayAccession,
                                   final ApiProperty[] assayProperties) throws ResourceNotFoundException {
        Assay assay = findAssay(experimentAccession, assayAccession);

        for (ApiProperty apiAssayProperty : assayProperties) {
            Property property = getOrCreateProperty(apiAssayProperty);

            List<OntologyTerm> terms = Lists.newArrayList();
            for (ApiOntologyTerm apiOntologyTerm : apiAssayProperty.getTerms()) {
                terms.add(getOrCreateOntologyTerm(apiOntologyTerm));
            }

            assay.addOrUpdateProperty(property, terms);
        }

        assayDAO.save(assay);
    }

    /**
     * Removes assayProperties from assay: assayAccession in experiment: experimentAccession
     *
     * @param experimentAccession
     * @param assayAccession
     * @param assayProperties
     * @throws ResourceNotFoundException if experiment: experimentAccession or assay: assayAccession in that experiment are not found
     */
    @Transactional
    public void deleteAssayProperties(final String experimentAccession,
                                      final String assayAccession,
                                      final ApiProperty[] assayProperties) throws ResourceNotFoundException {
        Assay assay = findAssay(experimentAccession, assayAccession);

        for (ApiProperty apiProperty : assayProperties) {
            assay.deleteProperty(getOrCreateProperty(apiProperty));
        }

        assayDAO.save(assay);
    }

    /**
     * @param experimentAccession
     * @param sampleAccession
     * @return Collection of ApiSampleProperty from sample: sampleAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or sample: sampleAccession
     *                                   in that experiment are not found
     */
    @Transactional
    public Collection<ApiProperty> getSampleProperties(
            final String experimentAccession,
            final String sampleAccession)
            throws ResourceNotFoundException {
        Sample sample = findSample(experimentAccession, sampleAccession);
        return new ApiSample(sample).getProperties();
    }


    /**
     * Adds (or updates mapping to efo terms for) sampleProperties to sample: sampleAccession in experiment: experimentAccession
     *
     * @param experimentAccession
     * @param sampleAccession
     * @param sampleProperties
     * @throws ResourceNotFoundException if experiment: experimentAccession or sample: sampleAccession
     *                                   in that experiment are not found
     */
    @Transactional
    public void putSampleProperties(final String experimentAccession,
                                    final String sampleAccession,
                                    final ApiProperty[] sampleProperties) throws ResourceNotFoundException {
        Sample sample = findSample(experimentAccession, sampleAccession);

        for (ApiProperty apiSampleProperty : sampleProperties) {
            List<OntologyTerm> terms = Lists.newArrayList();
            for (ApiOntologyTerm apiOntologyTerm : apiSampleProperty.getTerms()) {
                terms.add(getOrCreateOntologyTerm(apiOntologyTerm));
            }

            sample.addOrUpdateProperty(getOrCreateProperty(apiSampleProperty), terms);
        }

        sampleDAO.save(sample);
    }

    /**
     * Deletes sampleProperties from sample: sampleAccession in experiment: experimentAccession
     *
     * @param experimentAccession
     * @param sampleAccession
     * @param sampleProperties
     * @throws ResourceNotFoundException if experiment: experimentAccession or sample: sampleAccession
     *                                   in that experiment are not found
     */
    @Transactional
    public void deleteSampleProperties(final String experimentAccession,
                                       final String sampleAccession,
                                       final ApiProperty[] sampleProperties) throws ResourceNotFoundException {
        Sample sample = findSample(experimentAccession, sampleAccession);

        for (ApiProperty apiSampleProperty : sampleProperties) {
            sample.deleteProperty(getOrCreateProperty(apiSampleProperty));
        }

        sampleDAO.save(sample);
    }

    /**
     * @param ontologyName
     * @return ApiOntology corresponding to ontologyName
     * @throws ResourceNotFoundException if ontology: ontologyName was not found
     */
    public ApiOntology getOntology(final String ontologyName) throws ResourceNotFoundException {
        try {
            Ontology ontology = ontologyDAO.getByName(ontologyName);
            return new ApiOntology(ontology);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * Adds or updates details for Ontology corresponding to apiOntology
     *
     * @param apiOntology
     */
    @Transactional
    public void putOntology(@Nonnull final ApiOntology apiOntology) {
        try {
            Ontology ontology = ontologyDAO.getByName(apiOntology.getName());
            ontology.setDescription(apiOntology.getDescription());
            ontology.setName(apiOntology.getName());
            ontology.setVersion(apiOntology.getVersion());
            ontology.setSourceUri(apiOntology.getSourceUri());
            ontologyDAO.save(ontology);
        } catch (RecordNotFoundException e) { // ontology not found - create a new one
            getOrCreateOntology(apiOntology);
        }
    }

    /**
     * @param ontologyTermAcc
     * @return ApiOntologyTerm corresponding to ontologyTerm
     * @throws ResourceNotFoundException if ontology term: ontologyTerm was not found
     */
    public ApiOntologyTerm getOntologyTerm(final String ontologyTermAcc) throws ResourceNotFoundException {

        try {
            OntologyTerm ontologyTerm = ontologyTermDAO.getByName(ontologyTermAcc);
            return new ApiOntologyTerm(ontologyTerm);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * Add (or update mappings to Ontology for) apiOntologyTerms
     *
     * @param apiOntologyTerms
     */
    @Transactional
    public void putOntologyTerms(final ApiOntologyTerm[] apiOntologyTerms) {
        for (ApiOntologyTerm apiOntologyTerm : apiOntologyTerms) {
            try {
                OntologyTerm ontologyTerm = ontologyTermDAO.getByName(apiOntologyTerm.getAccession());
                ontologyTerm.setAccession(apiOntologyTerm.getAccession());
                ontologyTerm.setDescription(apiOntologyTerm.getDescription());
                ontologyTerm.setOntology(getOrCreateOntology(apiOntologyTerm.getOntology()));
                ontologyTerm.setTerm(apiOntologyTerm.getTerm());
                ontologyTermDAO.save(ontologyTerm);
            } catch (RecordNotFoundException e) {
                // ontology term not found - create a new one
                getOrCreateOntologyTerm(apiOntologyTerm);
            }
        }
    }


    /**
     * @param apiOntology
     * @return existing Ontology corresponding to apiOntology.getName(); otherwise a new Ontology corresponding to apiOntology
     */
    private Ontology getOrCreateOntology(@Nonnull ApiOntology apiOntology) {
        return ontologyDAO.getOrCreateOntology(
                apiOntology.getName(),
                apiOntology.getDescription(),
                apiOntology.getSourceUri(),
                apiOntology.getVersion());
    }

    /**
     * @param apiOntologyTerm
     * @return existing OntologyTerm corresponding to apiOntologyTerm.getAccession(); otherwise a new OntologyTerm
     *         corresponding to apiOntologyTerm
     */
    private OntologyTerm getOrCreateOntologyTerm(@Nonnull ApiOntologyTerm apiOntologyTerm) {

        Ontology ontology = getOrCreateOntology(apiOntologyTerm.getOntology());

        return ontologyTermDAO.getOrCreateOntologyTerm(
                apiOntologyTerm.getAccession(),
                apiOntologyTerm.getTerm(),
                apiOntologyTerm.getDescription(),
                ontology);

    }


    /**
     * @param experimentAccession
     * @param assayAccession
     * @return Assay corresponding to assayAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or assay: assayAccession
     *                                   in that experiment are not found
     */
    private Assay findAssay(final String experimentAccession, final String assayAccession) throws ResourceNotFoundException {
        try {
            final Experiment experiment = atlasDAO.getExperimentByAccession(experimentAccession);
            return experiment.getAssay(assayAccession);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }

    /**
     * @param experimentAccession
     * @param sampleAccession
     * @return Sample corresponding to sampleAccession in experiment: experimentAccession
     * @throws ResourceNotFoundException if experiment: experimentAccession or sample: sampleAccession
     *                                   in that experiment are not found
     */
    private Sample findSample(final String experimentAccession, final String sampleAccession) throws ResourceNotFoundException {
        try {
            final Experiment experiment = atlasDAO.getExperimentByAccession(experimentAccession);
            return experiment.getSample(sampleAccession);
        } catch (RecordNotFoundException e) {
            throw convert(e);
        }
    }


    private Property getOrCreateProperty(ApiProperty apiAssayProperty) {
        return getOrCreateProperty(apiAssayProperty.getName(), apiAssayProperty.getValue());
    }

    private Property getOrCreateProperty(String name, String value) {
        final PropertyName propertyName = propertyDAO.getOrCreateProperty(name);
        final PropertyValue propertyValue = propertyValueDAO.getOrCreatePropertyValue(value);
        return new Property(propertyName, propertyValue);
    }

    private PropertyName getOrCreatePropertyName(String name) {
        return propertyDAO.getOrCreateProperty(name);
    }

    private PropertyValue getOrCreatePropertyValue(String value) {
        return propertyValueDAO.getOrCreatePropertyValue(value);
    }

    private static ResourceNotFoundException convert(RecordNotFoundException e) {
        return new ResourceNotFoundException(e.getMessage(), e);
    }
}
