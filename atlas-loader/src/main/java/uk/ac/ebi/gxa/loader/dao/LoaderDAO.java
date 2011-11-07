package uk.ac.ebi.gxa.loader.dao;

import uk.ac.ebi.gxa.dao.*;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.microarray.atlas.model.*;

/**
 * The bridge between loader and the rest of the application - encapsulates external services
 * used during data loading
 */
public class LoaderDAO {
    private final ExperimentDAO experimentDAO;
    private final PropertyValueDAO propertyValueDAO;
    private final PropertyDAO propertyDAO;
    private final OrganismDAO organismDAO;
    private final ArrayDesignDAO arrayDesignDAO;

    public LoaderDAO(ExperimentDAO experimentDAO, PropertyDAO propertyDAO, PropertyValueDAO propertyValueDAO, OrganismDAO organismDAO, ArrayDesignDAO arrayDesignDAO) {
        this.experimentDAO = experimentDAO;
        this.propertyDAO = propertyDAO;
        this.propertyValueDAO = propertyValueDAO;
        this.organismDAO = organismDAO;
        this.arrayDesignDAO = arrayDesignDAO;
    }

    public Organism getOrCreateOrganism(String name) {
        return organismDAO.getOrCreateOrganism(name);
    }

    /**
     *
     * @param name  Free-form string describing EF
     * @param value Free-form string describing EFV
     * @return PropertyValue corresponding to the values passed
     */
    public Property getOrCreatePropertyValue(String name, String value) {
        final PropertyName propertyName = propertyDAO.getOrCreateProperty(name);
        final PropertyValue propertyValue = propertyValueDAO.getOrCreatePropertyValue(value);
        return new Property(propertyName, propertyValue);
    }

    public ArrayDesign getArrayDesignShallow(String accession) {
        return arrayDesignDAO.getArrayDesignShallowByAccession(accession);
    }

    public ArrayDesign getArrayDesign(String accession) {
        return arrayDesignDAO.getArrayDesignByAccession(accession);
    }

    public void save(Experiment experiment) {
        experimentDAO.save(experiment);
    }

    public Experiment getExperiment(String accession) throws RecordNotFoundException {
        return experimentDAO.getByName(accession);
    }
}
