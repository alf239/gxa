package uk.ac.ebi.gxa.statistics;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rpetry
 * Date: Oct 26, 2010
 * Time: 5:31:40 PM
 * Serializable representation of ef-efv for the purpose of ConciseSet storage
 */
public class Attribute implements Serializable {
    private String value;
    private transient boolean isEfo;
    private transient StatisticsType statType;

    public Attribute(final String value) {
        this.value = value.intern();
    }

    public Attribute(final String value, final boolean isEfo, final StatisticsType statType) {
        this.value = value.intern();
        this.isEfo = isEfo;
        this.statType = statType;
    }

    public String getEfv() {
        return value;
    }

    public void setEfv(final String value) {
        this.value = value.intern();
    }

    public boolean isEfo() {
        return isEfo;
    }

    public void setEfo(boolean efo) {
        isEfo = efo;
    }

    public StatisticsType getStatType() {
        return statType;
    }

    public void setStatType(StatisticsType statType) {
        this.statType = statType;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        if (value != null ? !value.equals(attribute.value) : attribute.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
