package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.microarray.atlas.model.*;

import java.util.List;

public class AssayDAO extends AbstractDAO<Assay> {
    public static final String NAME_COL = "accession";

    public static final Logger log = LoggerFactory.getLogger(AssayDAO.class);

    public AssayDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Assay.class);
    }

    long getTotalCount() {
        return (Long) template.find("select count(a) FROM Assay a").get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Assay> getAssaysByPropertyValue(PropertyName name, PropertyValue value) {
        return template.find("select a from Experiment e " +
                "left join e.assays a " +
                "left join a.properties p " +
                "where p.property = ? and p.propertyValue = ? ", name, value);
    }

    @Override
    public void save(Assay object) {
        super.save(object);
        template.flush();
    }

    /**
     * @return Name of the column for hibernate to match searched objects against - c.f. super.getByName()
     */
    @Override
    public String getNameColumn() {
        return NAME_COL;
    }

    @SuppressWarnings("unchecked")
    public List<Property> getProperties(PropertyName propertyName) {
        return template.find("select distinct " +
                "new uk.ac.ebi.microarray.atlas.model.Property(ap.property, ap.propertyValue) " +
                "from AssayProperty ap where ap.property = ?", propertyName);
    }

    @SuppressWarnings("unchecked")
    public List<PropertyName> getPropertyNames() {
        return template.find("select distinct ap.property from AssayProperty ap");
    }

    @SuppressWarnings("unchecked")
    public List<Property> getProperties() {
        return template.find("select distinct " +
                "new uk.ac.ebi.microarray.atlas.model.Property(ap.property, ap.propertyValue) " +
                "from AssayProperty ap");
    }

    @SuppressWarnings("unchecked")
    public List<AssayProperty> findProperties(PropertyName property, PropertyValue propertyValue) {
        return template.find("select ap from AssayProperty ap " +
                "where ap.property = ? and ap.propertyValue = ?", property, propertyValue);
    }
}
