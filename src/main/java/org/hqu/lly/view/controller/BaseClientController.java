package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.utils.UIUtil;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * 客户端控制器基类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/10/2 20:20
 */
@Slf4j
public abstract class BaseClientController<T extends BaseClient> implements Initializable {

    protected Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 长文本换行flag
     */
    protected boolean softWrap = false;

    /**
     * 发送设置面板
     */
    protected Stage sendSettingPane;

    /**
     * 定时发送服务
     */
    protected ScheduledSendService scheduledService;

    /**
     * 发送设置
     */
    protected ScheduledSendConfig sendConfig = new ScheduledSendConfig();
    /**
     * 定时任务服务
     */
    protected ScheduledTaskService scheduledTaskService;
    /**
     * 客户端服务
     */
    protected T client;
    /**
     * 当前面板的连接协议
     */
    protected String protocol;
    @FXML
    private TextField remoteAddressInput;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private ListView<Label> msgList;
    @FXML
    private TextArea msgInput;
    @FXML
    private Button sendMsgButton;
    @FXML
    private ToggleButton scheduleSendBtn;
    @FXML
    private ToggleButton softWrapBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button sendSettingBtn;

    public BaseClientController() {
        setProtocol();
        setClient();
    }

    /**
     * <p>
     *     为当前controller设置协议
     * </p>
     * @date 2022-10-23 18:38:41 <br>
     * @author hqully <br>
     */
    protected abstract void setProtocol();

    /**
     * <p>
     *     为当前controller设置客户端实体
     * </p>
     * @date 2022-10-23 18:38:51 <br>
     * @author hqully <br>
     */
    protected abstract void setClient();


    @FXML
    void confirmAddr(MouseEvent event) {
        URI uri = URI.create(protocol + remoteAddressInput.getText());
        client.setURI(uri);
        client.setService(new ClientService() {
            @Override
            public void onStart() {
                if (!errorMsgLabel.getText().isEmpty()) {
                    Platform.runLater(() -> {
                        errorMsgLabel.setText("");
                    });
                }
                setActiveUI();
            }

            @Override
            public void onError(Throwable e, String errorMessage) {
                Platform.runLater(() -> {
                    if (errorMessage != null) {
                        errorMsgLabel.setText(errorMessage);
                    } else {
                        errorMsgLabel.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onClose() {
                client.destroy();
                setInactiveUI();
            }

            @Override
            public void updateMsgList(String msg) {
                Label msgLabel = UIUtil.getMsgLabel(msg, UIUtil.getFixMsgLabelWidth(msgList.getWidth()), softWrap);
                Platform.runLater(() -> {
                    msgList.getItems().add(msgLabel);
                });
            }
        });

        FutureTask<Channel> channel = new FutureTask<>(client);
        executor.execute(channel);
        Platform.runLater(() -> {
            errorMsgLabel.setText("连接中...");
        });
    }

    @FXML
    void disconnect(MouseEvent event) {
        destroy();
        setInactiveUI();
    }


    @FXML
    void sendMsg(MouseEvent event) {
        client.sendMessage(msgInput.getText());
    }

    @FXML
    void clearMsg(MouseEvent event) {
        msgList.getItems().remove(0, msgList.getItems().size());
        msgList.refresh();
    }

    @FXML
    void handleSoftWrap(MouseEvent event) {
        softWrap = !softWrap;
        double labelWidth = softWrap ? UIUtil.getFixMsgLabelWidth(msgList.getWidth()) : Region.USE_COMPUTED_SIZE;
        ObservableList<Label> msgItems = msgList.getItems();
        msgItems.forEach(msg -> {
            UIUtil.changeMsgLabel(msg, labelWidth, softWrap);
        });
        Platform.runLater(() -> {
            msgList.setItems(msgItems);
        });
    }

    @FXML
    void scheduleSend(MouseEvent event) {
        if (scheduleSendBtn.isSelected()) {
            scheduledService = new ScheduledSendService(sendConfig, scheduledTaskService);
            scheduledService.start();
        }
        if (!scheduleSendBtn.isSelected()) {
            scheduledService.cancel();
        }
    }

    @FXML
    void showSendSetting(MouseEvent event) {
        sendSettingPane.show();
    }

    public void destroy() {
        // 如果有定时任务正在执行,则取消
        if (scheduledService != null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        client.destroy();
    }

    protected void setActiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(true);
            msgInput.setDisable(false);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            sendMsgButton.setDisable(false);
            scheduleSendBtn.setDisable(false);
        });
    }

    protected void setInactiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(false);
            msgInput.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 定时任务设置
        scheduledTaskService = new ScheduledTaskService() {
            @Override
            public void onTaskStart() {
                scheduleSendBtn.setDisable(false);
            }

            @Override
            public void onAllTasksCompleted() {
                scheduleSendBtn.setSelected(false);
            }
        };
        sendConfig.setTaskFactory(new SendTaskFactory(new TaskService() {
            @Override
            public void fireTask() {
                client.sendMessage(msgInput.getText());
            }
        }));

        sendSettingPane = SendSettingPaneFactory.create(sendConfig);

        // 功能按钮悬浮tip提示
        initMsgSideBar();

        // 消息上下文菜单
        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }

    protected void initMsgSideBar() {
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));

    }

}
