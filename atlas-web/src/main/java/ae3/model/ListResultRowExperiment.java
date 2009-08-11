package ae3.model;

import uk.ac.ebi.ae3.indexbuilder.Expression;
import ae3.restresult.RestOut;

/**
 * @author pashky
 */
public class ListResultRowExperiment {
    private long experimentId;
    private String experimentAccession;
    private String experimentDescription;
    private String experimentName;
    private Expression updn;
    private double pvalue;

    /**
     * Constructor
     * @param experimentId experiment id
     * @param experimentName experimnet name
     * @param experimentAccession experiment accessment
     * @param experimentDescription experimnet description
     * @param pvalue p-value
     * @param updn up or down
     */
    public ListResultRowExperiment(long experimentId, String experimentName, String experimentAccession, String experimentDescription,
                                   double pvalue, Expression updn) {
        this.experimentId = experimentId;
        this.experimentAccession = experimentAccession;
        this.experimentDescription = experimentDescription;
        this.experimentName = experimentName;
        this.pvalue = pvalue;
        this.updn = updn;
    }

    /**
     * Returns p-value
     * @return p-value
     */
    @RestOut(name="pvalue")
    public double getPvalue() {
        return pvalue;
    }

    /**
     * Returns experiment id
     * @return experiment id
     */
    public long getExperimentId() {
        return experimentId;
    }

    /**
     * Return experiment accession
     * @return experiment accession
     */
    @RestOut(name="accession")
    public String getExperimentAccession() {
        return experimentAccession;
    }

    /**
     * Returns experiment description
     * @return experiment description
     */
    @RestOut(name="description")
    public String getExperimentDescription() {
        return experimentDescription;
    }

    /**
     * Returns experiment name
     * @return experiment name
     */
    @RestOut(name="name")
    public String getExperimentName() {
        return experimentName;
    }

    /**
     * Returns up or down
     * @return UP or DOWN
     */
    @RestOut(name="expression")
    public Expression getUpdn() {
        return updn;
    }
}
