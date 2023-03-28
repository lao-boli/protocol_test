package org.hqu.lly.service;

/**
 * <p>
 * 针对定时任务的服务类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/29 15:23
 */
public interface ScheduledTaskService {

    /**
     * <p>
     * 任务开始的回调
     * </p>
     *
     * @date 2022-09-29 15:10:01 <br>
     */
    void onTaskStart();

    /**
     * <p>
     * 所有任务都完成后的回调
     * </p>
     *
     * @date 2022-09-29 10:35:53 <br>
     */
    void onAllTasksCompleted();

}
