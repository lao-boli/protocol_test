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
import org.hqu.lly.protocol.udp.server.UDPServer;
import org.hqu.lly.service.impl.ConnectionlessServerService;
import org.hqu.lly.utils.UIUtil;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * UDP服务端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 09:15
 */
public class UDPServerController implements Initializable {

    ObservableList<InetSocketAddress> AddressList = FXCollections.observableArrayList();
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
    private ToggleButton softWrapBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private ListView<Label> msgList;
    @FXML
    private ListView<InetSocketAddress> clientListBox;
    private UDPServer server = new UDPServer();
    private InetSocketAddress targetClientAddr = null;
    private boolean softWrap;

    @FXML
    void startServer(MouseEvent event) {
        int port = Integer.parseInt(serverPort.getText());
        server.setPort(port);
        server.setService(new ConnectionlessServerService() {
            @Override
            public void addInetSocketAddress(InetSocketAddress dstAddr) {
                Platform.runLater(() -> {
                    AddressList.add(dstAddr);
                    clientListBox.setItems(AddressList);
                });
            }

            @Override
            public void removeInetSocketAddress(InetSocketAddress dstAddr) {
                Platform.runLater(() -> {
                    AddressList.remove(dstAddr);
                    clientListBox.setItems(AddressList);
                });

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
        if (targetClientAddr != null) {
            server.sendMessage(msgInput.getText(), targetClientAddr);
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
        clientListBox.setCellFactory(channelListView -> new InetSocketAddressCellFactory());
        // 点击时将当前的clientAddr设置为选中的dstAddr
        clientListBox.getSelectionModel().selectedItemProperty().addListener((observableValue, preChannel, currentChannel) -> targetClientAddr = currentChannel);

        softWrapBtn.setTooltip(UIUtil.getTooltip("soft-wrap", 300));
        clearBtn.setTooltip(UIUtil.getTooltip("clear-all", 300));

        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
    }

    static class InetSocketAddressCellFactory extends ListCell<InetSocketAddress> {

        @Override
        protected void updateItem(InetSocketAddress dstAddr, boolean empty) {
            super.updateItem(dstAddr, empty);
            Platform.runLater(() -> {
                if (dstAddr != null) {
                    setText(dstAddr.toString());
                } else {
                    setText(null);
                }
            });
        }

    }

}
