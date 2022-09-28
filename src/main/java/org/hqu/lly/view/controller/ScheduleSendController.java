package org.hqu.lly.view.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.StageBridger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 定时发送消息弹出框控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 17:37
 */
@Slf4j
public class ScheduleSendController implements Initializable {

    private static final Integer TIMES = 0;
    private static final Integer MANUAL_STOP = 1;
    private final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    @Setter
    private StageBridger bridger;
    @FXML
    private TextField intervalTextField;
    @FXML
    private TextField sendCountTextField;
    @FXML
    private ToggleGroup sendSetting;
    @FXML
    private RadioButton sendByTimesBtn;
    @FXML
    private RadioButton manualStopBtn;
    @FXML
    private ToggleButton sendBtn;
    /**
     * 总共发送次数
     */
    private Integer sendCount = 0;
    /**
     * 发送间隔,单位为ms.
     */
    private Integer interval;
    /**
     * 发送类型,默认为手动停止.
     */
    private Integer sendType = MANUAL_STOP;
    private ScheduledService<Void> currentScheduledService;

    @FXML
    void handleScheduleSend(MouseEvent event) {
        if (sendBtn.isSelected()) {
            setSendingMode();
            if (sendType.equals(TIMES)) {
                currentScheduledService = getTimesScheduleService();
                currentScheduledService.start();
            }
            if (sendType.equals(MANUAL_STOP)) {
                currentScheduledService = getScheduledService();
                currentScheduledService.start();
            }
        } else {
            currentScheduledService.cancel();
            setPrepareMode();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendSetting.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == sendByTimesBtn) {
                    sendType = TIMES;
                }
                if (newValue == manualStopBtn) {
                    sendType = MANUAL_STOP;
                }
            }
        });
    }

    private ScheduledService<Void> getScheduledService() {
        ScheduledService<Void> scheduledService = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                sendMsgTask sendMsgTask = new sendMsgTask();
                sendMsgTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        Throwable e = sendMsgTask.getException();
                        log.error(e.toString());
                    }
                });
                return sendMsgTask;
            }
        };
        scheduledService.setExecutor(executorService);
        interval = Integer.parseInt(intervalTextField.getText());
        scheduledService.setPeriod(new Duration(interval));
        return scheduledService;
    }

    private ScheduledService<Void> getTimesScheduleService() {
        // 获取定时任务
        ScheduledService<Void> scheduledService = getScheduledService();
        // 设置发送次数
        sendCount = Integer.valueOf(sendCountTextField.getText());
        // 设置按次数发送消息回调
        scheduledService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if (sendCount > 0) {
                    sendCount = 0;
                }
                setPrepareMode();
            }
        });
        scheduledService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if (sendCount > 0) {
                    sendCount--;
                } else {
                    scheduledService.cancel();
                    sendBtn.setSelected(false);
                    setPrepareMode();
                }
            }
        });
        return scheduledService;
    }

    private void setPrepareMode() {
        sendByTimesBtn.setDisable(false);
        manualStopBtn.setDisable(false);
        intervalTextField.setDisable(false);
    }

    private void setSendingMode() {
        sendByTimesBtn.setDisable(true);
        manualStopBtn.setDisable(true);
        intervalTextField.setDisable(true);
    }

    /**
     * 通知服务类controller发送消息的任务类
     */
    private class sendMsgTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            log.info("run");
            bridger.fireTask();
            return null;
        }

    }

}
