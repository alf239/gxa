package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.microarray.atlas.model.PropertyValue;

import java.util.List;

public class PropertyValueDAO extends AbstractDAO<PropertyValue> {
    private PropertyDAO propertyDAO;

    public PropertyValueDAO(SessionFactory sessionFactory, PropertyDAO propertyDAO) {
        super(sessionFactory, PropertyValue.class);
        this.propertyDAO = propertyDAO;
    }

    /**
     * @param value value to search for
     * @return PropertyValue matching property:value
     * @throws uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException
     *          if no PropertyValue matching property:value was found
     */
    public PropertyValue find(String value) throws RecordNotFoundException {
        @SuppressWarnings("unchecked")
        final List<PropertyValue> results = template.find("from PropertyValue where value = ?", value);
        return getOnly(results);
    }

    /**
     * Not implemented
     *
     * @return nothing. Throws {@link UnsupportedOperationException}
     */
    @Override
    public String getNameColumn() {
        throw new UnsupportedOperationException();
    }

    public PropertyValue getOrCreatePropertyValue(String value) {
        try {
            return find(value);
        } catch (RecordNotFoundException e) {
            // property value not found - create a new one
            PropertyValue propertyValue = new PropertyValue(value);
            save(propertyValue);
            return propertyValue;
        }
    }
}
