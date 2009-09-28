package uk.ac.ebi.microarray.atlas.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.microarray.atlas.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A data access object designed for retrieving common sorts of data from the
 * atlas database.  This DAO should be configured with a spring {@link
 * JdbcTemplate} object which will be used to query the database.
 *
 * @author Tony Burdett
 * @date 21-Sep-2009
 */
public class AtlasDAO {
  // experiment queries
  private static final String EXPERIMENTS_SELECT =
      "SELECT accession, description, performer, lab, experimentid " +
          "FROM a2_experiment";
  private static final String EXPERIMENTS_PENDING_INDEX_SELECT =
      EXPERIMENTS_SELECT + " " +
          "WHERE something something"; // fixme: load monitor table?
  private static final String EXPERIMENTS_PENDING_NETCDF_SELECT =
      EXPERIMENTS_SELECT + " " +
          "WHERE something something"; // fixme: load monitor table?
  private static final String EXPERIMENT_BY_ACC_SELECT =
      EXPERIMENTS_SELECT + " " +
          "WHERE accession=?";

  // gene queries
  private static final String GENES_SELECT =
      "SELECT geneid, identifier, name, species " +
          "FROM a2_gene";
  private static final String GENES_PENDING_SELECT =
      GENES_SELECT + " " +
          "WHERE something something"; // fixme: load monitor table?
  private static final String GENES_BY_EXPERIMENT_ACCESSION =
      GENES_SELECT + " " +
          "WHERE experiment_id_key=?"; // fixme: linking genes to experiments?
  private static final String PROPERTIES_BY_GENEID =
      "SELECT " +
          "gp.name AS property, " +
          "gpv.value AS propertyvalue " +
          "FROM a2_geneproperty gp, a2_genepropertyvalue gpv " +
          "WHERE gpv.genepropertyid=gp.genepropertyid " +
          "AND gpv.geneid=?";

  // assay queries
  private static final String ASSAYS_BY_EXPERIMENT_ACCESSION =
      "SELECT a.accession, a.experimentid, a.arraydesignid " +
          "FROM a2_assay a, a2_experiment e " +
          "WHERE e.experimentid=a.experimentid " +
          "AND e.accession=?";
  private static final String PROPERTIES_BY_ASSAY_ACCESSION =
      "SELECT " +
          "p.name AS property, " +
          "p.accession, " +
          "pv.name AS propertyvalue, " +
          "apv.isfactorvalue " +
          "FROM " +
          "a2_property p, " +
          "a2_propertyvalue pv, " +
          "a2_assaypropertyvalue apv, " +
          "a2_assay a " +
          "WHERE apv.propertyvalueid=pv.propertyvalueid " +
          "AND pv.propertyid=p.propertyid " +
          "AND apv.assayid=a.assayid " +
          "AND a.accession=?";

  // sample queries
  private static final String SAMPLES_BY_ASSAY_ACCESSION =
      "SELECT s.accession, s.species, s.channel " +
          "FROM a2_sample s, a2_assay a, a2_assaysample ass " +
          "WHERE s.sampleid=ass.sampleid " +
          "AND a.assayid=ass.assayid " +
          "AND a.accession=?";
  private static final String PROPERTIES_BY_SAMPLE_ACCESSION =
      "SELECT " +
          "p.name AS property, " +
          "p.accession, " +
          "pv.name AS propertyvalue, " +
          "spv.isfactorvalue " +
          "FROM " +
          "a2_property p, " +
          "a2_propertyvalue pv, " +
          "a2_samplepropertyvalue spv, " +
          "a2_sample s " +
          "WHERE spv.propertyvalueid=pv.propertyvalueid " +
          "AND pv.propertyid=p.propertyid " +
          "AND spv.sampleid=s.sampleid " +
          "AND s.accession=?";

  // other atlas analytics queries
  private static final String DESIGN_ELEMENTS_BY_ARRAY_ACCESSION =
      "SELECT de.accession from A2_ARRAYDESIGN ad, A2_DESIGNELEMENT de " +
          "WHERE de.arraydesignid=ad.arraydesignid" +
          "AND ad.accession=?";
  private static final String EXPRESSIONANALYTICS_BY_GENEID =
      "SELECT ef.name AS ef, efv.name AS efv, a.experimentid, a.tstat a.pvaladj " +
          "FROM a2_expressionanalytics a " +
          "JOIN a2_propertyvalue efv ON efv.propertyvalueid=a.propertyvalueid " +
          "JOIN a2_property ef ON ef.propertyid=efv.propertyid " +
          "JOIN a2_designelement de ON de.designelementid=a.designelementID " +
          "WHERE de.geneid=?";
  private static final String ONTOLOGY_MAPPINGS_BY_ONTOLOGYNAME =
      // fixme - work out the new query, this is for old schema
      "select experiment_id_key||'_'||ef||'_'||efv as mapkey, string_agg(accession) from (SELECT DISTINCT s.experiment_id_key," +
          "     LOWER(SUBSTR(oa.orig_value_src,    instr(oa.orig_value_src,    '_',    1,    3) + 1,    instr(oa.orig_value_src,    '__DM',    1,    1) -instr(oa.orig_value_src,    '_',    1,    3) -1)) ef," +
          "     oa.orig_value AS efv," +
          "     oa.accession" +
          "   FROM ontology_annotation oa," +
          "     ae1__sample__main s" +
          "   WHERE(s.sample_id_key = oa.sample_id_key OR s.assay_id_key = oa.assay_id_key)" +
          "   AND oa.ontology_id_key = 575119145) group by experiment_id_key, ef, efv";

  private JdbcTemplate template;

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public List<Experiment> getAllExperiments() {
    List results = template.query(EXPERIMENTS_SELECT,
                                  new ExperimentMapper());
    return (List<Experiment>) results;
  }

  public List<Experiment> getAllExperimentsPendingIndexing() {
    List results = template.query(EXPERIMENTS_PENDING_INDEX_SELECT,
                                  new ExperimentMapper());
    return (List<Experiment>) results;
  }

  public List<Experiment> getAllExperimentsPendingNetCDFs() {
    List results = template.query(EXPERIMENTS_PENDING_NETCDF_SELECT,
                                  new ExperimentMapper());
    return (List<Experiment>) results;
  }

  /**
   * Gets a single experiment from the Atlas Database, queried by the accession
   * of the experiment.
   *
   * @param accession the experiment's accession number (usually in the format
   *                  E-ABCD-1234)
   * @return an object modelling this experiment
   */
  public Experiment getExperimentByAccession(String accession) {
    List results = template.query(EXPERIMENT_BY_ACC_SELECT,
                                  new Object[]{accession},
                                  new ExperimentMapper());

    return results.size() > 0 ? (Experiment) results.get(0) : null;
  }

  public List<Gene> getAllGenes() {
    List results = template.query(GENES_SELECT,
                                  new GeneMapper());
    return (List<Gene>) results;
  }

  public List<Gene> getAllPendingGenes() {
    List results = template.query(GENES_PENDING_SELECT,
                                  new GeneMapper());
    return (List<Gene>) results;
  }

  public List<Gene> getGenesByExperimentAccession(String exptAccession) {
    List results = template.query(GENES_BY_EXPERIMENT_ACCESSION,
                                  new Object[]{exptAccession},
                                  new GeneMapper());
    return (List<Gene>) results;

  }

  public void getPropertiesForGenes(List<Gene> genes) {
    // also fetch all properties
    for (Gene gene : genes) {
      // fixme: this is inefficient - we'll end up generating lots of queries.  Is it better to handle with a big join?
      List propResults = template.query(PROPERTIES_BY_GENEID,
                                        new Object[]{gene.getGeneID()},
                                        new GenePropertyMapper());
      // and set on assay
      gene.setProperties(propResults);
    }
  }

  public List<Assay> getAssaysByExperimentAccession(
      String experimentAccession) {
    List results = template.query(ASSAYS_BY_EXPERIMENT_ACCESSION,
                                  new Object[]{experimentAccession},
                                  new AssayMapper());

    List<Assay> assays = (List<Assay>) results;

    // also fetch all properties
    for (Assay assay : assays) {
      // fixme: this is inefficient - we'll end up generating lots of queries.  Is it better to handle with a big join?
      List propResults = template.query(PROPERTIES_BY_ASSAY_ACCESSION,
                                        new Object[]{assay.getAccession()},
                                        new PropertyMapper());
      // and set on assay
      assay.setProperties(propResults);
    }

    return assays;
  }

  public List<Sample> getSamplesByAssayAccession(String assayAccession) {
    List results = template.query(SAMPLES_BY_ASSAY_ACCESSION,
                                  new Object[]{assayAccession},
                                  new SampleMapper());

    List<Sample> samples = (List<Sample>) results;
    // also fetch all properties
    for (Sample sample : samples) {
      // fixme: this is inefficient - we'll end up generating lots of queries.  Is it better to handle with a big join?
      List propResults = template.query(PROPERTIES_BY_SAMPLE_ACCESSION,
                                        new Object[]{sample.getAccession()},
                                        new PropertyMapper());
      // and set on assay
      sample.setProperties(propResults);
    }

    return samples;
  }

  /**
   * A convenience method that fetches the set of design element accessions by
   * array design accession.  The set of design element ids contains no
   * duplicates, and the results that are returned are the manufacturers
   * accession strings for each design element on an array design.  The
   * parameters accepted are a {@link java.sql.Connection} to a database
   * following the atlas schema, and the accession string of the array design
   * (which should be of the form A-ABCD-123).
   *
   * @param arrayDesignAccession the accession number of the array design to
   *                             query for
   * @return a set of unique design element accession strings
   */
  public List<String> getDesignElementsByArrayAccession(
      String arrayDesignAccession) {
    List results = template.query(DESIGN_ELEMENTS_BY_ARRAY_ACCESSION,
                                  new Object[]{arrayDesignAccession},
                                  new DesignElementMapper());
    return (List<String>) results;
  }

  public List<ExpressionAnalytics> getExpressionAnalyticsByGeneID(
      String geneID) {
    List results = template.query(EXPRESSIONANALYTICS_BY_GENEID,
                                  new Object[]{geneID},
                                  new ExpressionAnalyticsMapper());
    return (List<ExpressionAnalytics>) results;
  }

  public List<OntologyMapping> getOntologyMappingsForOntology(
      String ontologyName) {
    List results = template.query(ONTOLOGY_MAPPINGS_BY_ONTOLOGYNAME,
                                  new Object[]{ontologyName},
                                  new OntologyMappingMapper());
    return (List<OntologyMapping>) results;
  }

  private class ExperimentMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      Experiment experiment = new Experiment();

      experiment.setAccession(resultSet.getString(1));
      experiment.setDescription(resultSet.getString(2));
      experiment.setPerformer(resultSet.getString(3));
      experiment.setLab(resultSet.getString(4));
      experiment.setExperimentID(resultSet.getString(5));

      return experiment;
    }
  }

  private class GeneMapper implements RowMapper {
    public Gene mapRow(ResultSet resultSet, int i) throws SQLException {
      Gene gene = new Gene();

      gene.setGeneID(resultSet.getString(1));
      gene.setIdentifier(resultSet.getString(2));
      gene.setName(resultSet.getString(3));
      gene.setSpecies(resultSet.getString(4));

      return gene;
    }
  }

  private class AssayMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      Assay assay = new Assay();

      assay.setAccession(resultSet.getString(1));
      assay.setExperimentAccession(resultSet.getString(2));
      assay.setArrayDesignAcession(resultSet.getString(3));

      return assay;
    }
  }

  private class SampleMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      Sample sample = new Sample();

      sample.setAccession(resultSet.getString(1));
      sample.setSpecies(resultSet.getString(2));
      sample.setChannel(resultSet.getString(3));

      return sample;
    }
  }

  private class DesignElementMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i)
        throws SQLException {
      return resultSet.getString(1);
    }
  }

  private class ExpressionAnalyticsMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      ExpressionAnalytics ea = new ExpressionAnalytics();

      ea.setEfName(resultSet.getString(1));
      ea.setEfvName(resultSet.getString(2));
      ea.setExperimentID(resultSet.getLong(3));
      ea.setTStatistic(resultSet.getDouble(4));
      ea.setPValAdjusted(resultSet.getDouble(5));

      return ea;
    }
  }

  private class OntologyMappingMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      OntologyMapping mapping = new OntologyMapping();

      mapping.setExperimentID(resultSet.getString(1));
      mapping.setEfName(resultSet.getString(2));
      mapping.setEfvName(resultSet.getString(3));
      // quick bit of sugar to reformat single ,/; separated string into an array
      mapping.setOntologyTermAccessions(resultSet.getString(4).split("[,;]"));

      return mapping;
    }
  }

  private class PropertyMapper implements RowMapper {
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      Property property = new Property();

      property.setName(resultSet.getString(1));
      property.setAccession(resultSet.getString(2));
      property.setValue(resultSet.getString(3));
      property.setFactorValue(resultSet.getBoolean(4));

      return property;
    }
  }

  private class GenePropertyMapper implements RowMapper {

    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
      Property property = new Property();

      property.setName(resultSet.getString(1));
      property.setValue(resultSet.getString(2));
      property.setFactorValue(false);

      return property;
    }
  }
}
