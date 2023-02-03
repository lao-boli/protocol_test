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
import org.hqu.lly.domain.bean.CustomDataConfig;
import org.hqu.lly.domain.bean.SendSettingConfig;
import org.hqu.lly.domain.config.ClientConfig;
import org.hqu.lly.domain.config.TabConfig;
import org.hqu.lly.exception.UnSetBoundException;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.utils.DataUtil;
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
public abstract class BaseClientController<T extends BaseClient> extends BaseController implements Initializable {

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


    protected SendSettingConfig sendSettingConfig = new SendSettingConfig();

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

    /**
     * 客户端面板配置类
     */
    protected ClientConfig clientConfig;

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

    public void initByConfig(ClientConfig config) {
        remoteAddressInput.setText(config.getServerAddr());
        msgInput.setText(config.getMsgInput());
        sendSettingConfig = config.getSendSettingConfig();
        sendSetting();
    }

    @Override
    public TabConfig saveAndGetConfig(){
        clientConfig = new ClientConfig();
        clientConfig.setProtocol(protocol);
        clientConfig.setMsgInput(msgInput.getText());
        clientConfig.setServerAddr(remoteAddressInput.getText());
        clientConfig.setSendSettingConfig(sendSettingConfig);
        return clientConfig;
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
        sendMsg();
    }

    private void sendMsg() {

        if ("未定义数据边界!".equals(errorMsgLabel.getText())){
            errorMsgLabel.setText("");
        }

        String text = msgInput.getText();
        if (sendSettingConfig.isTextMode()){
            client.sendMessage(text);
        }

        try {
            if (sendSettingConfig.isCustomMode()){
                CustomDataConfig customDataConfig = sendSettingConfig.getCustomDataConfig();
                String msg = DataUtil.createMsg(customDataConfig.getCustomDataPattern(), customDataConfig.getBoundList());
                client.sendMessage(msg);

            }
        } catch (UnSetBoundException e) {
            log.warn(e.getMessage());
            Platform.runLater(() -> {
                errorMsgLabel.setText("未定义数据边界!");
            });
        }
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
            scheduledService = new ScheduledSendService(sendSettingConfig.getScheduledSendConfig(), scheduledTaskService);
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

        // 发送设置
        sendSetting();

        sendSettingPane = SendSettingPaneFactory.create(sendSettingConfig);

        // 功能按钮悬浮tip提示
        initMsgSideBar();

        // 消息上下文菜单
        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }

    private void sendSetting() {
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
        sendSettingConfig.getScheduledSendConfig().setTaskFactory(new SendTaskFactory(new TaskService() {
            @Override
            public void fireTask() {
                sendMsg();
            }
        }));

        // 发送模式改变时的回调
        sendSettingConfig.setOnModeChange(new TaskService() {
            @Override
            public void fireTask() {
                if (sendSettingConfig.isTextMode()) {
                    Platform.runLater(() -> {
                        msgInput.setDisable(false);
                    });
                }
                if (sendSettingConfig.isCustomMode()) {
                    Platform.runLater(() -> {
                        msgInput.setDisable(true);
                    });
                }
            }
        });
    }

    protected void initMsgSideBar() {
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));

    }

    public void updateConfig(ClientConfig config){
        SendSettingConfig sendSettingConfig = config.getSendSettingConfig();
        this.sendSettingConfig.setMode(sendSettingConfig.getMode());
        this.sendSettingConfig.getScheduledSendConfig().setSendTimes(sendSettingConfig.getScheduledSendConfig().getSendTimes());


    }

}
