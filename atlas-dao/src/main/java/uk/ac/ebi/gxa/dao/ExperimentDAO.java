package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.gxa.exceptions.LogUtil;
import uk.ac.ebi.microarray.atlas.model.Experiment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class ExperimentDAO extends AbstractDAO<Experiment> {
    public static final String NAME_COL = "accession";

    public static final Logger log = LoggerFactory.getLogger(ExperimentDAO.class);

    public ExperimentDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Experiment.class);
    }

    @SuppressWarnings("unchecked")
    public List<Experiment> getExperimentsByArrayDesignAccession(String accession) {
        return template.find("select e from Experiment e left join e.assays a where a.arrayDesign.accession = ? ", accession);
    }

    long getTotalCount() {
        return (Long) template.find("select count(e) FROM Experiment e").get(0);
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
    public void delete(String experimentAccession) throws RecordNotFoundException {
        template.delete(getByName(experimentAccession, NAME_COL));
    }

    public void delete(Experiment experiment) {
        template.delete(experiment);
    }

    @Override
    public void save(Experiment object) {
        super.save(object);
        template.flush();
    }

    /**
     * @return Name of the column for hibernate to match searched objects against - c.f. super.getByName()
     */
    @Override
    public String getNameColumn() {
        return NAME_COL;
    }


    public List<Experiment> getExperimentsPreparedForIndexing() {
        preloadDetails();
        return getAll();
    }

    /**
     * An awful hack preloading experiment details in order to make indexing fast. Only needed for indexing.
     * <p/>
     * DO NOT use it anywhere else: it's useless for anything but full index.
     */
    private void preloadDetails() {
        final List<?> sp = template.find("select sp from SampleProperty sp left join fetch sp.terms");
        log.debug("{} sample properties", sp.size());
        final List<?> ap = template.find("select ap from AssayProperty ap left join fetch ap.terms");
        log.debug("{} assay properties", ap.size());
    }
}
