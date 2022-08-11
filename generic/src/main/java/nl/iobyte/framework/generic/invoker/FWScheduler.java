package nl.iobyte.framework.generic.invoker;

import nl.iobyte.framework.generic.invoker.enums.TaskType;

import java.util.concurrent.TimeUnit;

public interface FWScheduler {

    /**
     * Run task
     *
     * @param r Runnable
     */
    void run(Runnable r);

    /**
     * Run task with type
     *
     * @param r    Runnable
     * @param type TaskType
     */
    void run(Runnable r, TaskType type);

    /**
     * Run task later
     *
     * @param r       Runnable
     * @param timeout Long
     * @param unit    TimeUnit
     */
    void runLater(Runnable r, long timeout, TimeUnit unit);

    /**
     * Run task later with type
     *
     * @param r       Runnable
     * @param timeout Long
     * @param unit    TimeUnit
     * @param type    TaskType
     */
    void runLater(Runnable r, long timeout, TimeUnit unit, TaskType type);

    /**
     * Run task repeating
     *
     * @param r       Runnable
     * @param timeout Long
     * @param unit    TimeUnit
     */
    void runRepeating(Runnable r, long timeout, TimeUnit unit);

    /**
     * Run task repeating with type
     *
     * @param r       Runnable
     * @param timeout Long
     * @param unit    TimeUnit
     * @param type    TaskType
     */
    void runRepeating(Runnable r, long timeout, TimeUnit unit, TaskType type);

    /**
     * Run task sync
     *
     * @param r Runnable
     */
    void runSync(Runnable r);

}
