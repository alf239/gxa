package uk.ac.ebi.microarray.atlas.model.bioentity;

import javax.persistence.*;

/**
 * User: nsklyar
 * Date: 04/05/2011
 */
@Entity
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "swSeq")
    @SequenceGenerator(name = "swSeq", sequenceName = "A2_SOFTWARE_SEQ")
    private Long softwareid;
    private String name;
    private String version;
    // TODO: Ticket #3117 to enable proper handling of isActive field via the Admin UI
    private Boolean isActive = false;

    Software() {
    }

    public Software(Long softwareid, String name, String version) {
        this.softwareid = softwareid;
        this.name = name;
        this.version = version;
    }

    public Software(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public Long getSoftwareid() {
        return softwareid;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Software{" +
                "softwareid=" + softwareid +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Software software = (Software) o;

        if (name != null ? !name.equals(software.name) : software.name != null) return false;
        if (version != null ? !version.equals(software.version) : software.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
