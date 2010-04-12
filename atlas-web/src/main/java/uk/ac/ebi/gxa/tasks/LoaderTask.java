/*
 * Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */
package uk.ac.ebi.gxa.tasks;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.gxa.loader.listener.AtlasLoaderEvent;
import uk.ac.ebi.gxa.loader.listener.AtlasLoaderListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author pashky
 */
public class LoaderTask extends AbstractWorkingTask {
    private static Logger log = LoggerFactory.getLogger(LoaderTask.class);

    public static final String TYPE_EXPERIMENT = "loadexperiment";
    public static final String TYPE_ARRAYDESIGN = "loadarraydesign";    

    private static class TaskInternalError extends Exception { }

    boolean stop;

    public void start() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                if(getRunMode() == TaskRunMode.CONTINUE && TaskStatus.DONE.equals(getCurrentStatus())) {
                    taskMan.writeTaskLog(LoaderTask.this, TaskEvent.SKIPPED, "");
                    taskMan.notifyTaskFinished(LoaderTask.this);
                    return;
                }

                stop = false;
                taskMan.updateTaskStage(getTaskSpec(), TaskStatus.INCOMPLETE);
                taskMan.writeTaskLog(LoaderTask.this, TaskEvent.STARTED, "");
                final AtomicReference<AtlasLoaderEvent> result = new AtomicReference<AtlasLoaderEvent>(null);

                try {
                    AtlasLoaderListener listener = new AtlasLoaderListener() {
                        public void loadSuccess(AtlasLoaderEvent event) {
                            synchronized (LoaderTask.this) {
                                result.set(event);
                                LoaderTask.this.notifyAll();
                            }
                        }

                        public void loadError(AtlasLoaderEvent event) {
                            synchronized (LoaderTask.this) {
                                result.set(event);
                                LoaderTask.this.notifyAll();
                            }
                        }

                        public void loadProgress(String progress) {
                            currentProgress = progress;
                        }
                    };

                    taskMan.getLoader().setPossibleQTypes(taskMan.getAtlasProperties().getPossibleQuantitaionTypes());

                    if(TYPE_EXPERIMENT.equals(getTaskSpec().getType()))
                        taskMan.getLoader().loadExperiment(new URL(getTaskSpec().getAccession()), listener);
                    else if(TYPE_ARRAYDESIGN.equals(getTaskSpec().getType()))
                        taskMan.getLoader().loadArrayDesign(new URL(getTaskSpec().getAccession()), listener);
                    else
                        throw new TaskInternalError();

                    synchronized (LoaderTask.this) {
                        while(result.get() == null) {
                            try {
                                LoaderTask.this.wait();
                            } catch(InterruptedException e) {
                                // continue
                            }
                        }
                    }

                    if(result.get().getStatus() == AtlasLoaderEvent.Status.SUCCESS) {
                        taskMan.writeTaskLog(LoaderTask.this, TaskEvent.FINISHED, "");
                        taskMan.updateTaskStage(getTaskSpec(), TaskStatus.DONE);

                        for(String accession : result.get().getAccessions()) {
                            if(TYPE_EXPERIMENT.equals(getTaskSpec().getType())) {
                                taskMan.addTaskTag(LoaderTask.this, TaskTagType.EXPERIMENT, accession);

                                TaskSpec experimentTask = new TaskSpec(AnalyticsTask.TYPE, accession);
                                taskMan.updateTaskStage(experimentTask, TaskStatus.INCOMPLETE);

                                if(!stop && isRunningAutoDependencies()) {
                                    taskMan.scheduleTask(
                                            LoaderTask.this,
                                            experimentTask,
                                            TaskRunMode.RESTART,
                                            getUser(),
                                            true,
                                            "Automatically added by experiment " + getTaskSpec().getAccession() + " loading task");
                                }
                            } else if(TYPE_ARRAYDESIGN.equals(getTaskSpec().getType())) {
                                taskMan.addTaskTag(LoaderTask.this, TaskTagType.ARRAYDESIGN, accession);

                                TaskSpec indexTask = new TaskSpec(IndexTask.TYPE, "");
                                taskMan.updateTaskStage(indexTask, TaskStatus.INCOMPLETE);
                                if(!stop && isRunningAutoDependencies()) {
                                    taskMan.scheduleTask(LoaderTask.this, indexTask, TaskRunMode.CONTINUE, getUser(), true,
                                            "Automatically added by array design " + getTaskSpec().getAccession() + " loading task");
                                }
                            } else
                                throw new TaskInternalError();
                        }
                    } else {
                        for(Throwable e : result.get().getErrors()) {
                            log.error("Task failed because of:", e);
                        }
                        taskMan.writeTaskLog(LoaderTask.this, TaskEvent.FAILED, StringUtils.join(result.get().getErrors(), '\n'));
                    }

                } catch(MalformedURLException e) {
                    taskMan.writeTaskLog(LoaderTask.this, TaskEvent.FAILED, "Invalid URL " + getTaskSpec().getAccession());
                } catch(TaskInternalError e) {
                    taskMan.writeTaskLog(LoaderTask.this, TaskEvent.FAILED, "Impossible happened");
                }

                taskMan.notifyTaskFinished(LoaderTask.this); // it's waiting for this
            }
        });
        thread.setName("LoaderTaskThread-" + getTaskSpec() + "-" + getTaskId());
        thread.start();
    }

    public void stop() {
        stop = true;
    }

    private LoaderTask(final TaskManager queue, final Task prototype) {
        super(queue, prototype);
        taskMan.addTaskTag(LoaderTask.this, TaskTagType.URL, getTaskSpec().getAccession());
    }

    public static final WorkingTaskFactory FACTORY = new WorkingTaskFactory() {
        public WorkingTask createTask(TaskManager queue, Task prototype) {
            return new LoaderTask(queue, prototype);
        }

        public boolean isForType(TaskSpec taskSpec) {
            return TYPE_EXPERIMENT.equals(taskSpec.getType()) || TYPE_ARRAYDESIGN.equals(taskSpec.getType());
        }

        public boolean isBlockedBy(TaskSpec what, TaskSpec by) {
            return false;
        }
    };
    
}
