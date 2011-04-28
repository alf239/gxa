package uk.ac.ebi.gxa.loader.service;

import java.util.Date;

import uk.ac.ebi.gxa.loader.DataReleaseCommand;
import uk.ac.ebi.gxa.netcdf.reader.AtlasNetCDFDAO;
import uk.ac.ebi.gxa.Model;
import uk.ac.ebi.gxa.Experiment;

import uk.ac.ebi.gxa.exceptions.LogUtil;

public class AtlasDataReleaseService {
    private Model atlasModel;
    private AtlasNetCDFDAO atlasNetCDFDAO;

    public void process(DataReleaseCommand command) {
        final String accession = command.getAccession();
        try {
            getAtlasNetCDFDAO().releaseExperiment(accession);
            final Experiment experiment = atlasModel.getExperimentByAccession(accession);
            experiment.setReleaseDate(new Date());
            experiment.save();
        } catch (Exception ex) {
            throw LogUtil.logUnexpected("Can not release data for experiment " + accession, ex);
        }
    }

    public void setAtlasModel(Model atlasModel) {
        this.atlasModel = atlasModel;
    }

    public AtlasNetCDFDAO getAtlasNetCDFDAO() {
        return atlasNetCDFDAO;
    }

    public void setAtlasNetCDFDAO(AtlasNetCDFDAO atlasNetCDFDAO) {
        this.atlasNetCDFDAO = atlasNetCDFDAO;
    }
}
