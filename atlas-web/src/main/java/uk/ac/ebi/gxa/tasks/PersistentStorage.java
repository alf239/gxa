package uk.ac.ebi.gxa.tasks;

/**
 * @author pashky
 */
public interface PersistentStorage {

    public void logTaskStageEvent(TaskSpec task, TaskStage stage, TaskStageEvent event, String message);

    public void updateTaskStage(TaskSpec task, TaskStage stage);

    public TaskStage getTaskStage(TaskSpec task);

    public void logTaskOperation(TaskSpec task, TaskRunMode runMode, TaskUser user, TaskOperation operation, String message);
}
