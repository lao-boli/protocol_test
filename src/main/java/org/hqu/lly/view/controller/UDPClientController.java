package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.protocol.udp.client.UDPClient;
import org.hqu.lly.service.impl.ClientService;

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
    private Button disconnectButton;

    @FXML
    private ListView<String> msgList;

    private UDPClient client = new UDPClient();

    private String protocol = ProtocolConsts.UDP;

    ObservableList<String> items = FXCollections.observableArrayList();

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
                    items.add(msg);
                    msgList.setItems(items);
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
        });
    }

    @FXML
    void sendMsg(MouseEvent event) {
        client.sendMessage(msgInput.getText());

    }

    public void destroy() {
        client.destroy();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
