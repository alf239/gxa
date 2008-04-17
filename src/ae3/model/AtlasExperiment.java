package ae3.model;

import org.apache.solr.common.SolrDocument;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ostolop
 * Date: Apr 17, 2008
 * Time: 9:31:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class AtlasExperiment {
    private String experimentId;
    private Collection<String> experimentTypes;
    private String experimentAccession;
    private String experimentDescription;
    private Collection<String> experimentFactorValues;
    private Collection<String> experimentFactors;
    private Map<String, List<String>> experimentHighlights;

    public AtlasExperiment(SolrDocument exptDoc) {
        this.setExperimentId((String) exptDoc.getFieldValue("exp_id"));
        this.setExperimentTypes(exptDoc.getFieldValues("exp_type"));
        this.setExperimentAccession((String) exptDoc.getFieldValue("exp_accession"));
        this.setExperimentDescription((String) exptDoc.getFieldValue("exp_description"));
        this.setExperimentFactorValues(exptDoc.getFieldValues("exp_factor_value"));
        this.setExperimentFactors(exptDoc.getFieldValues("exp_factor"));
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public void setExperimentTypes(Collection experimentTypes) {
        this.experimentTypes = experimentTypes;
    }

    public void setExperimentAccession(String experimentAccession) {
        this.experimentAccession = experimentAccession;
    }

    public void setExperimentDescription(String experimentDescription) {
        this.experimentDescription = experimentDescription;
    }

    public void setExperimentFactorValues(Collection experimentFactorValues) {
        this.experimentFactorValues = experimentFactorValues;
    }

    public void setExperimentFactors(Collection experimentFactors) {
        this.experimentFactors = experimentFactors;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public Collection<String> getExperimentTypes() {
        return experimentTypes;
    }

    public String getExperimentAccession() {
        return experimentAccession;
    }

    public String getExperimentDescription() {
        return experimentDescription;
    }

    public Collection<String> getExperimentFactorValues() {
        return experimentFactorValues;
    }

    public Collection<String> getExperimentFactors() {
        return experimentFactors;
    }

    public void setExperimentHighlights(Map<String, List<String>> experimentHighlights) {
        this.experimentHighlights = experimentHighlights;
    }

    public Map<String, List<String>> getExperimentHighlights() {
        return experimentHighlights;
    }
}
