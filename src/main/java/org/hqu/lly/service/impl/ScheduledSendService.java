package org.hqu.lly.service.impl;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.config.ScheduledSendConfig;
import org.hqu.lly.service.ScheduledTaskService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 定时发送任务服务类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/28 19:37
 */
@Slf4j
public class ScheduledSendService {

    private static final Integer TIMES = 0;
    private static final Integer MANUAL_STOP = 1;

    private final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledSendConfig config;
    private ScheduledService<Void> scheduledService;
    private Integer sendCount;
    private ScheduledTaskService scheduledTaskService;

    public ScheduledSendService(ScheduledSendConfig config, ScheduledTaskService scheduledTaskService) {
        this.config = config;
        this.scheduledTaskService = scheduledTaskService;
        handleInit();
    }

    private void handleInit() {
        if (config.getSendType().equals(MANUAL_STOP)) {
            initScheduledService();
        }
        if (config.getSendType().equals(TIMES)) {
            sendCount = config.getSendTimes() - 1;
            initTimesScheduleService();
        }
    }

    private void initScheduledService() {
        scheduledService = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = config.getTaskFactory().create();
                return task;
            }
        };
        scheduledService.setExecutor(executorService);
        scheduledService.setPeriod(new Duration(config.getInterval()));
        scheduledService.setRestartOnFailure(false);
        scheduledService.setOnFailed(event -> log.info(event.toString()));
    }

    private void initTimesScheduleService() {
        // 获取定时任务
        initScheduledService();
        // 设置按次数发送消息回调
        scheduledService.setOnCancelled(event -> {
            if (sendCount > 0) {
                sendCount = 0;
            }
        });
        scheduledService.setOnSucceeded(event -> {
            if (sendCount > 0) {
                sendCount--;
            } else {
                scheduledService.cancel();
                scheduledTaskService.onAllTasksCompleted();
            }
        });
    }

    public void start() {
        scheduledService.start();
        scheduledTaskService.onTaskStart();
    }

    public void cancel() {
        scheduledService.cancel();
    }

    public boolean isRunning() {
        return scheduledService.isRunning();
    }

}
