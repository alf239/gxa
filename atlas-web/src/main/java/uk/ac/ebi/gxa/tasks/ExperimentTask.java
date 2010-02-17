package uk.ac.ebi.gxa.tasks;

import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.gxa.analytics.generator.listener.AnalyticsGenerationEvent;
import uk.ac.ebi.gxa.analytics.generator.listener.AnalyticsGeneratorListener;
import uk.ac.ebi.gxa.netcdf.generator.listener.NetCDFGenerationEvent;
import uk.ac.ebi.gxa.netcdf.generator.listener.NetCDFGeneratorListener;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author pashky
 */
public class ExperimentTask implements WorkingTask {
    private static final String TYPE = "experiment";

    private final TaskManager queue;
    private final TaskRunMode runMode;
    private final TaskSpec taskSpec;
    private volatile boolean stop;
    private volatile TaskStage currentStage;

    private enum Stage {
        NETCDF {
            public boolean run(final ExperimentTask task) {
                final AtomicReference<NetCDFGenerationEvent> result = new AtomicReference<NetCDFGenerationEvent>(null);
                task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.STARTED, "");
                task.queue.getNetcdfGenerator().generateNetCDFsForExperiment(task.taskSpec.getAccession(),
                        new NetCDFGeneratorListener() {
                            public void buildSuccess(NetCDFGenerationEvent event) {
                                synchronized (task) {
                                    result.set(event);
                                    task.notifyAll();
                                }
                            }

                            public void buildError(NetCDFGenerationEvent event) {
                                synchronized (task) {
                                    result.set(event);
                                    task.notifyAll();
                                }
                            }
                        });
                synchronized (task) {
                    while(result.get() == null)
                        try {
                            task.wait();
                        } catch (InterruptedException e) {
                            // skip
                        }
                }
                if(result.get().getStatus() == NetCDFGenerationEvent.Status.SUCCESS) {
                    task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.FINISHED, "");
                    return true;
                } else {
                    task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.FAILED, "");
                    return false;
                }
            }
        },

        ANALYTICS {
            public boolean run(final ExperimentTask task) {
                final AtomicReference<AnalyticsGenerationEvent> result = new AtomicReference<AnalyticsGenerationEvent>(null);
                task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.STARTED, "");
                task.queue.getAnalyticsGenerator().generateAnalyticsForExperiment(task.taskSpec.getAccession(),
                        new AnalyticsGeneratorListener() {
                            public void buildSuccess(AnalyticsGenerationEvent event) {
                                synchronized (task) {
                                    result.set(event);
                                    task.notifyAll();
                                }
                            }

                            public void buildError(AnalyticsGenerationEvent event) {
                                synchronized (task) {
                                    result.set(event);
                                    task.notifyAll();
                                }
                            }
                        });
                synchronized (task) {
                    while(result.get() == null)
                        try {
                            task.wait();
                        } catch (InterruptedException e) {
                            // skip
                        }
                }
                if(result.get().getStatus() == AnalyticsGenerationEvent.Status.SUCCESS) {
                    task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.FINISHED, "Successfully");
                    return true;
                } else {
                    task.queue.writeTaskLog(task.getTaskSpec(), stage(), TaskStageEvent.FAILED, StringUtils.join(result.get().getErrors(), '\n'));
                    return false;
                }
            }
        },

        DONE {
            public boolean run(ExperimentTask task) {
                return true; // what's done is done, do nothing
            }
        };

        abstract boolean run(ExperimentTask task);
        TaskStage stage() { return TaskStage.valueOf(this); }
    }

    public void start() {
        TaskStage currentStatus = queue.getTaskStage(getTaskSpec());

        final Stage fromStage;
        if(runMode == TaskRunMode.CONTINUE) {
            if(TaskStage.DONE.equals(currentStatus)) {
                currentStage = currentStatus;
                queue.notifyTaskFinished(ExperimentTask.this);
                return; // do nothing, "continue" fired on finished task by mistake
            }
            if(TaskStage.NONE.equals(currentStatus))
                fromStage = Stage.values()[0]; // continue from nothing = start from scratch
            else
                fromStage = Stage.valueOf(currentStatus.getStage()); // current status = stage, which is to be completed
        } else
            fromStage = Stage.values()[0];

        stop = false;
        if(fromStage == Stage.DONE)
            return;

        Thread thread = new Thread(new Runnable() {
            public void run() {
                for(int stageId = fromStage.ordinal(); stageId < Stage.values().length; ++stageId) {
                    Stage stage = Stage.values()[stageId];
                    currentStage = TaskStage.valueOf(stage);
                    ExperimentTask.this.queue.updateTaskStage(getTaskSpec(), currentStage); // here we are, setting stage which is about to start
                    if(stop) {
                        // we've been stopped in the meanwhile, so do not continue and generate a "fail" event
                        queue.writeTaskLog(getTaskSpec(), currentStage, TaskStageEvent.STOPPED, "Stopped by user request");
                        break;
                    }

                    boolean doContinue = stage.run(ExperimentTask.this);
                    if(!doContinue)
                        break;
                }
                queue.notifyTaskFinished(ExperimentTask.this); // it's waiting for this
            }
        });
        thread.setName(taskSpec.toString());
        thread.start();
    }

    private ExperimentTask(final TaskManager queue, final TaskSpec taskSpec, final TaskRunMode runMode) {
        this.queue = queue;
        this.taskSpec = taskSpec;
        this.runMode = runMode;
    }

    public TaskSpec getTaskSpec() {
        return taskSpec;
    }

    public TaskStage getCurrentStage() {
        return currentStage;
    }

    public void stop() {
        stop = true;
    }

    public static final WorkingTaskFactory FACTORY = new WorkingTaskFactory() {
        public WorkingTask createTask(TaskManager queue, TaskSpec taskSpec, TaskRunMode runMode) {
            return new ExperimentTask(queue, taskSpec, runMode);
        }

        public boolean isForType(String type) {
            return TYPE.equals(type);
        }
    };


}
