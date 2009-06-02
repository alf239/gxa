package uk.ac.ebi.ae3.indexbuilder;

import java.io.Serializable;

/**
 * @author pashky
 */
public class Experiment implements Serializable, Comparable<Experiment> {
    private Expression expression;
    private long id;
    private String ef;
    private String efv;
    private String[] efo;
    private double pvalue;

    Experiment(Expression expression, long id, String ef, String efv, String[] efo, double pvalue) {
        this.expression = expression;
        this.id = id;
        this.ef = ef;
        this.efv = efv;
        this.efo = efo;
        this.pvalue = pvalue;
    }

    public Expression getExpression() {
        return expression;
    }

    public long getId() {
        return id;
    }

    public String getEf() {
        return ef;
    }

    public String getEfv() {
        return efv;
    }

    public String[] getEfo() {
        return efo;
    }

    public double getPvalue() {
        return pvalue;
    }

    public int compareTo(Experiment o) {
        return Double.valueOf(o.pvalue).compareTo(pvalue);
    }
}
