package org.activityinfo.service.tasks;

import org.activityinfo.model.record.RecordBeanClass;

/**
 *
 * Executes
 *
 * @param <T>
 */
public interface TaskExecutor<T extends TaskModel> {

    /**
     *
     * @return this task's model class
     */
    RecordBeanClass<T> getModelClass();

    /**
     *
     * @param task
     * @return a human-readable description of the task
     */
    String describe(T task) throws Exception;

    /**
     * Executes the task.
     *
     * @param context
     * @param task
     * @return a TaskModel-specific result encoded as a record.
     * @throws Exception
     */
    void execute(TaskContext context, T task) throws Exception;

}
