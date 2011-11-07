package uk.ac.ebi.microarray.atlas.model;

import org.apache.commons.lang.IncompleteArgumentException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import uk.ac.ebi.gxa.utils.EscapeUtil;
import uk.ac.ebi.gxa.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

@Entity
@Table(name = "A2_PROPERTY")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
public final class PropertyName implements Comparable<PropertyName> {
    @Id
    @Column(name = "PROPERTYID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "propertySeq")
    @SequenceGenerator(name = "propertySeq", sequenceName = "A2_PROPERTY_SEQ", allocationSize = 1)
    private Long propertyid;
    @Nonnull
    private String name;
    private String displayName;

    PropertyName() {
    }

    private PropertyName(Long id, String accession, String displayName) {
        if (!accession.equals(getSanitizedPropertyAccession(accession)))
            throw new IncompleteArgumentException("Property accession must be sanitized");

        this.propertyid = id;
        this.name = accession;
        this.displayName = displayName;
    }

    public static String getSanitizedPropertyAccession(String name) {
        return EscapeUtil.encode(name).toLowerCase();
    }

    public static PropertyName createProperty(String displayName) {
        return createProperty(null, getSanitizedPropertyAccession(displayName), displayName);
    }

    public static PropertyName createProperty(@Nullable Long id, String accession, String displayName) {
        return new PropertyName(id, accession, displayName);
    }

    public Long getId() {
        return propertyid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName == null ? StringUtil.prettify(name) : displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyName that = (PropertyName) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + propertyid +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(PropertyName o) {
        return name.compareTo(o.name);
    }
}
