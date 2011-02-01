package uk.ac.ebi.gxa.statistics;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rpetry
 * Date: Oct 26, 2010
 * Time: 5:33:41 PM
 * Serializable representation of an Atlas Experiment for the purpose of ConciseSet storage
 */
public class Experiment implements Serializable {
    private static final long serialVersionUID = 1101371981067364007L;
    private String accession;
    private String experimentId;

    // Used to store minimum pVal when retrieving ranked lists of experiments sorted (ASC) by pValue/tStat ranks wrt to a specific ef(-efv) combination
    PvalTstatRank pValTstatRank;


    public Experiment(final String accession, final String experimentId) {
        this.accession = accession.intern();
        this.experimentId = experimentId.intern();
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(final String accession) {
        this.accession = accession.intern();
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId.intern();
    }

    public PvalTstatRank getpValTStatRank() {
        return pValTstatRank;
    }

    public void setPvalTstatRank(PvalTstatRank pValTstatRank) {
        this.pValTstatRank = pValTstatRank;
    }

    @Override
    public String toString() {
        return "experimentId: " + experimentId + "; accession: " + accession;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (accession == null || !accession.equals(that.accession) || experimentId == null || !experimentId.equals(that.experimentId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + (experimentId != null ? experimentId.hashCode() : 0);
        return result;
    }
}

