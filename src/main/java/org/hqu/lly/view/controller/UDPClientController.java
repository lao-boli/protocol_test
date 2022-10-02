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
import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.protocol.udp.client.UDPClient;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.utils.UIUtil;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * UDP客户端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:31
 */
@Slf4j
public class UDPClientController implements Initializable {

    @FXML
    private TextField remoteAddressInput;
    @FXML
    private Button connectButton;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private TextArea msgInput;
    @FXML
    private Button sendMsgButton;
    @FXML
    private ToggleButton scheduleSendBtn;
    @FXML
    private Button disconnectButton;
    @FXML
    private ToggleButton softWrapBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button sendSettingBtn;
    @FXML
    private ListView<Label> msgList;
    private UDPClient client = new UDPClient();
    private String protocol = ProtocolConsts.UDP;
    private boolean softWrap = false;
    private Stage sendSettingPane;
    private ScheduledSendService scheduledService;
    private ScheduledSendConfig sendConfig = new ScheduledSendConfig();
    private ScheduledTaskService scheduledTaskService;

    @FXML
    void confirmAddr(MouseEvent event) {
        URI uri = URI.create(protocol + remoteAddressInput.getText());
        client.setURI(uri);
        client.setService(new ClientService() {
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
                Platform.runLater(() -> {
                    Label msgLabel = UIUtil.getMsgLabel(msg, msgList.getWidth() - 20, softWrap);
                    msgList.getItems().add(msgLabel);
                });
            }
        });


        FutureTask<Channel> channel = new FutureTask<Channel>(client);

        new Thread(channel).start();
        try {
            if (channel.get() != null && channel.get().isActive()) {
                if (!errorMsgLabel.getText().isEmpty()) {
                    errorMsgLabel.setText("");
                }
                setActiveUI();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setActiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(true);
            msgInput.setDisable(false);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            sendMsgButton.setDisable(false);
            scheduleSendBtn.setDisable(false);
        });
    }

    @FXML
    void disconnect(MouseEvent event) {
        destroy();
        setInactiveUI();
    }

    private void setInactiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(false);
            msgInput.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
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
        double labelWidth = softWrap ? msgList.getWidth() - 20 : Region.USE_COMPUTED_SIZE;
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
        if (scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        client.destroy();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        softWrapBtn.setTooltip(UIUtil.getTooltip("soft-wrap", 300));
        clearBtn.setTooltip(UIUtil.getTooltip("clear-all", 300));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("send-setting", 300));

        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }

}
