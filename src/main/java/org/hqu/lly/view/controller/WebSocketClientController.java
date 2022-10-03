package org.hqu.lly.view.controller;

import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.protocol.websocket.client.WebSocketClient;

/**
 * <p>
 * WebSocket客户端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:36
 */
public class WebSocketClientController extends BaseClientController<WebSocketClient>{

    @Override
    protected void setProtocol() {
        protocol = ProtocolConsts.WEB_SOCKET;
    }

    @Override
    protected void setClient() {
        client = new WebSocketClient();
    }
/*
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

    private WebSocketClient client = new WebSocketClient();
    private String protocol = ProtocolConsts.WEB_SOCKET;

    private boolean softWrap;

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
                destroy();
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
        client.destroy();
        setInactiveUI();
    }

    private void setInactiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(false);
            msgInput.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(false);
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
        double labelWidth = softWrap ? UIUtil.getFixMsgLabelWidth(msgList.getWidth()): Region.USE_COMPUTED_SIZE;
        ObservableList<Label> msgItems = msgList.getItems();
        msgItems.forEach(msg -> {
            UIUtil.changeMsgLabel(msg, labelWidth, softWrap);
        });
        Platform.runLater(() -> {
            msgList.setItems(msgItems);
        });
    }

    public void destroy() {
        if (scheduledService!= null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        client.destroy();
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
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));

        // 消息上下文菜单
        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }*/

}
