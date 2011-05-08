package uk.ac.ebi.gxa.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.microarray.atlas.model.Asset;
import uk.ac.ebi.microarray.atlas.model.Experiment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AssetDAO extends AbstractDAO<Asset> {
    private ExperimentDAO edao;

    public AssetDAO(JdbcTemplate template) {
        super(template);
    }

    public void setExperimentDAO(ExperimentDAO edao) {
        this.edao = edao;
    }

    @Override
    protected Asset loadById(long id) {
        return template.queryForObject("select " + AssetMapper.FIELDS + " from 2_experimentasset " +
                "where EXPERIMENTASSETID = ?",
                new Object[]{id},
                new AssetMapper());
    }

    @Override
    protected String sequence() {
        return "A2_EXPERIMENTASSET_SEQ";
    }

    @Override
    protected void save(Asset object) {
        // TODO: 4alf: implement
        throw new IllegalStateException("not implemented");
    }

    public List<Asset> getByExperiment(Experiment experiment) {
        return template.query(
                "SELECT " + AssetMapper.FIELDS + " FROM a2_experimentasset " +
                        " WHERE experimentid = ? " +
                        " ORDER BY experimentAssetID",
                new Object[]{experiment.getId()},
                new AssetMapper()
        );
    }

    private class AssetMapper implements RowMapper<Asset> {
        private static final String FIELDS = "EXPERIMENTASSETID, EXPERIMENTID, NAME, FILENAME, DESCRIPTION";

        public Asset mapRow(ResultSet rs, int i) throws SQLException {
            Asset asset = new Asset(rs.getLong(1), edao.getById(rs.getLong(2)), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            registerObject(asset.getId(), asset);
            return asset;
        }
    }
}
