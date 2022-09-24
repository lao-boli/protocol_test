package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.hqu.lly.protocol.tcp.server.TCPServer;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.utils.UIUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * TCP服务端功能控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 09:15
 */
public class TCPServerController implements Initializable {

    ObservableList<Channel> channelList = FXCollections.observableArrayList();
    @FXML
    private TextField serverPort;
    @FXML
    private Button confirmButton;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private TextArea msgInput;
    @FXML
    private Button sendMsgButton;
    @FXML
    private Button closeServerButton;
    @FXML
    private ListView<Label> msgList;
    @FXML
    private ListView<Channel> clientListBox;
    @FXML
    private ToggleButton softWrapBtn;
    @FXML
    private Button clearBtn;
    private TCPServer server = new TCPServer();
    private Channel targetClientChannel = null;
    private boolean softWrap;


    @FXML
    void startServer(MouseEvent event) {
        int port = Integer.parseInt(serverPort.getText());
        server.setPort(port);
        server.setService(new ConnectedServerService() {
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

            }

            @Override
            public void addChannel(Channel channel) {
                Platform.runLater(() -> {
                    channelList.add(channel);
                    clientListBox.setItems(channelList);
                });
            }

            @Override
            public void removeChannel(Channel channel) {
                Platform.runLater(() -> {
                    channelList.remove(channel);
                    clientListBox.setItems(channelList);
                });
            }

            @Override
            public void updateMsgList(String msg) {
                Platform.runLater(() -> {
                    Label msgLabel = UIUtil.getMsgLabel(msg, msgList.getWidth() - 20, softWrap);
                    msgList.getItems().add(msgLabel);
                });
            }
        });

        FutureTask<Channel> channel = new FutureTask<>(server);

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

    @FXML
    void closeServer(MouseEvent event) {
        server.destroy();
        setInactiveUI();
    }

    private void setActiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(true);
            msgInput.setDisable(false);
            confirmButton.setDisable(true);
            closeServerButton.setDisable(false);
            sendMsgButton.setDisable(false);
        });
    }

    private void setInactiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(false);
            msgInput.setDisable(true);
            confirmButton.setDisable(false);
            closeServerButton.setDisable(true);
            sendMsgButton.setDisable(true);
        });
    }

    @FXML
    void sendMsg(MouseEvent event) {
        if (targetClientChannel != null) {
            server.sendMessage(msgInput.getText(), targetClientChannel);
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
        double labelWidth = softWrap ? msgList.getWidth() - 20 : Region.USE_COMPUTED_SIZE;
        ObservableList<Label> msgItems = msgList.getItems();
        msgItems.forEach(msg -> {
            UIUtil.changeMsgLabel(msg, labelWidth, softWrap);
        });
        Platform.runLater(() -> {
            msgList.setItems(msgItems);
        });
    }

    public void destroy() {
        server.destroy();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 自定义细胞工厂，设置显示的内容
        clientListBox.setCellFactory(channelListView -> new ChannelCellFactory());
        // 点击时将当前的clientChannel设置为选中的channel
        clientListBox.getSelectionModel().selectedItemProperty().addListener((observableValue, preChannel, currentChannel) -> targetClientChannel = currentChannel);

        softWrapBtn.setTooltip(UIUtil.getTooltip("soft-wrap", 300));
        clearBtn.setTooltip(UIUtil.getTooltip("clear-all", 300));

        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }


    static class ChannelCellFactory extends ListCell<Channel> {

        @Override
        protected void updateItem(Channel channel, boolean empty) {
            super.updateItem(channel, empty);
            Platform.runLater(() -> {
                if (channel != null) {
                    setText(channel.remoteAddress().toString());
                } else {
                    setText(null);
                }
            });
        }

    }

}
