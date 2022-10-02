package org.hqu.lly.factory;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.TaskService;

/**
 * <p>
 * 发送消息任务工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/28 20:40
 */
@Slf4j
public class SendTaskFactory implements TaskFactory<Void> {

    private final TaskService taskService;

    public SendTaskFactory(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public Task<Void> create() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                taskService.fireTask();
                return null;
            }
        };
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Throwable e = task.getException();
                log.error(e.toString());
            }
        });
        return task;
    }

}
