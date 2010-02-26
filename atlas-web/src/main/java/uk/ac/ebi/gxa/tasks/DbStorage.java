package uk.ac.ebi.gxa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.ebi.gxa.dao.AtlasDAO;

/**
 * Oracle DB-backed storage for {@link TaskManager} class
 * @author pashky
 */
public class DbStorage implements PersistentStorage {
    private Logger log = LoggerFactory.getLogger(getClass());
    private JdbcTemplate jdbcTemplate;

    public void setDao(AtlasDAO dao) {
        this.jdbcTemplate = dao.getJdbcTemplate();
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateTaskStage(TaskSpec task, TaskStage stage) {
        try {
            if(jdbcTemplate.update(
                    "MERGE INTO A2_TASKMAN_TASKSTAGE ts USING DUAL ON (ts.type = :1 and ts.accession = :2) " +
                            "WHEN MATCHED THEN UPDATE SET stage = :3 " +
                            "WHEN NOT MATCHED THEN INSERT (type,accession,stage) values (:1, :2, :3)",
                    new Object[] {
                            task.getType(),
                            "".equals(task.getAccession()) ? " " : task.getAccession(),
                            stage.toString()
                    }) != 1)
                log.error("Can't store task stage " + task + " " + stage);
        } catch (DataAccessException e) {
            log.error("Can't store task stage " + task + " " + stage, e);
        }
    }

    public TaskStage getTaskStage(TaskSpec task) {
        try {
            return TaskStage.valueOf(jdbcTemplate.queryForObject(
                    "SELECT stage FROM A2_TASKMAN_TASKSTAGE ts WHERE ts.type = :1 AND ts.accession = :2",
                    new Object[] {
                            task.getType(),
                            "".equals(task.getAccession()) ? " " : task.getAccession()
                    }, String.class).toString());
        } catch (EmptyResultDataAccessException e) {
            return TaskStage.NONE;
        } catch (DataAccessException e) {
            log.error("Can't retrieve task stage " + task, e);
            return TaskStage.NONE;
        }
    }

    public void logTaskStageEvent(TaskSpec task, TaskStage stage, TaskStageEvent event, String message) {
        try {
            if(jdbcTemplate.update(
                    "INSERT INTO A2_TASKMAN_TASKSTAGELOG (TYPE, ACCESSION, STAGE, EVENT, MESSAGE) VALUES (?,?,?,?,?)",
                    new Object[] {
                            task.getType(),
                            "".equals(task.getAccession()) ? " " : task.getAccession(),
                            stage.getStage(),
                            event.toString(),
                            message
                    }) != 1)
                log.error("Can't store task stage log " + task + " " + stage + " " + event + " " + message);
        } catch (DataAccessException e) {
            log.error("Can't store task stage log " + task + " " + stage + " " + event + " " + message, e);
        }
    }

    public void logTaskOperation(TaskSpec task, TaskRunMode runMode, TaskUser user, TaskOperation operation, String message) {
        try {
            if(jdbcTemplate.update(
                    "INSERT INTO A2_TASKMAN_OPERATIONLOG (TYPE, ACCESSION, RUNMODE, USERNAME, OPERATION, MESSAGE) VALUES (?,?,?,?,?,?)",
                    new Object[] {
                            task.getType(),
                            "".equals(task.getAccession()) ? " " : task.getAccession(),
                            runMode == null ? null : runMode.toString(),
                            user.getUserName(),
                            operation.toString(),
                            message == null ? null : message
                    }) != 1)
                log.error("Can't store task operation log " + task + " " + runMode + " " + operation + " " + " " + user + " " + message);
        } catch (DataAccessException e) {
            log.error("Can't store task operation log " + task + " " + runMode + " " + operation + " " + " " + user + " " + message, e);
        }
    }
}
