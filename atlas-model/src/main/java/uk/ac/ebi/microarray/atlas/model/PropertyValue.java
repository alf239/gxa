package uk.ac.ebi.microarray.atlas.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.annotation.Nonnull;
import javax.persistence.*;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
public final class PropertyValue implements Comparable<PropertyValue> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "propertyValueSeq")
    @SequenceGenerator(name = "propertyValueSeq", sequenceName = "A2_PROPERTYVALUE_SEQ", allocationSize = 1)
    private Long propertyvalueid;
    @Nonnull
    @Column(name = "NAME")
    private String value;
    private String displayName;

    PropertyValue() {
    }

    public PropertyValue(String value) {
        if (value == null)
            throw new NullPointerException("Value must be provided");

        this.value = value;
    }

    public Long getId() {
        return propertyvalueid;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayName == null ? value : displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyValue that = (PropertyValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "id=" + propertyvalueid +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int compareTo(PropertyValue o) {
        return value.compareTo(o.value);
    }
}
