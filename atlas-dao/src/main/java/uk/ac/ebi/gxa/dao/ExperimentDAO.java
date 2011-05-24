package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.gxa.exceptions.LogUtil;
import uk.ac.ebi.microarray.atlas.model.Experiment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExperimentDAO extends AbstractDAO<Experiment> {
    public static final Logger log = LoggerFactory.getLogger(ExperimentDAO.class);

    public ExperimentDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Experiment.class);
    }

    @SuppressWarnings("unchecked")
    public List<Experiment> getExperimentsByArrayDesignAccession(String accession) {
        return template.find("from Experiment left join assays a where a.arrayDesign.accession = ? ", accession);
    }

    long getTotalCount() {
        return (Long) template.find("select count(e) FROM Experiment e").get(0);
    }

    public Experiment getExperimentByAccession(String accession) {
        final List results = template.find("from Experiment where accession = ?", accession);
        return results.isEmpty() ? null : (Experiment) results.get(0);
    }

    public long getCountSince(String lastReleaseDate) {
        try {
            return (Long) template.find("select count(id) from Experiment where loadDate > ?",
                    new SimpleDateFormat("MM-yyyy").parse(lastReleaseDate)).get(0);
        } catch (ParseException e) {
            throw LogUtil.createUnexpected("Invalid date: " + lastReleaseDate, e);
        }
    }

    @Deprecated
    public void delete(String experimentAccession) {
        template.delete(getExperimentByAccession(experimentAccession));
    }

    public void delete(Experiment experiment) {
        template.delete(experiment);
    }
}
