package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.microarray.atlas.model.PropertyName;

public class PropertyDAO extends AbstractDAO<PropertyName> {

    public PropertyDAO(SessionFactory sessionFactory) {
        super(sessionFactory, PropertyName.class);
    }

    @Override
    public void save(PropertyName object) {
        super.save(object);
        template.flush();
    }

    @Override
    protected String getNameColumn() {
        return "name";
    }

    /**
     * @return lower case matching required in getByName() queries
     */
    @Override
    protected boolean lowerCaseNameMatch() {
        return true;
    }

    public PropertyName getOrCreateProperty(String displayName) {
        final String accession = PropertyName.getSanitizedPropertyAccession(displayName);
        try {
            return getByName(accession);
        } catch (RecordNotFoundException e) {
            // property not found - create a new one
            PropertyName propertyName = PropertyName.createProperty(null, accession, displayName);
            save(propertyName);
            return propertyName;
        }
    }
}
