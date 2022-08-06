package com.hqu.lly.view.controller;

import com.hqu.lly.protocol.tcp.client.TCPClient;
import com.hqu.lly.service.UIService;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * 主面板
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/3 20:07
 * @Version 1.0
 */
@Slf4j
public class MainController implements Initializable {
    @FXML
    private Button tcp;

    @FXML
    private TextField clientInput;

    @FXML
    private Button confirmButton;


    @FXML
    private ListView<String> msgList;
    @FXML
    private TreeView<String> menuTree;

    @FXML
    private TextArea msgInput;

    @FXML
    private Button sendMsgButton;

    @FXML
    private Button disconnectButton;

    private TCPClient tcpClient = new TCPClient();

    ObservableList<String> items = FXCollections.observableArrayList() ;

    @FXML
    void confirmAddr(MouseEvent event) {

        String[] hostAndPort = clientInput.getText().split(":");

        tcpClient.setHost(hostAndPort[0]);
        tcpClient.setPort(hostAndPort[1]);
        tcpClient.setUiService(new UIService() {
            @Override
            public void updateMsgList(String msg) {

                Platform.runLater(() -> {

                    items.add("received: " + msg);
                    msgList.setItems(items);
                });

            }
        });


        FutureTask<Channel> channel = new FutureTask<Channel>(tcpClient);

        new Thread(channel).start();
        try {
            channel.get();
            Platform.runLater(() ->{
                confirmButton.setDisable(true);
                disconnectButton.setDisable(false);
                sendMsgButton.setDisable(false);
            });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void disconnect(MouseEvent event) {

        tcpClient.destroy();

        Platform.runLater(() ->{
            confirmButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
        });
    }

    @FXML
    void sendMsg(MouseEvent event) {
        tcpClient.sendMessage(msgInput.getText());
        items.add("send:" + msgInput.getText());
        Platform.runLater(() -> {
            msgList.setItems(items);
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<String> root = new TreeItem<>("root");
        TreeItem<String> tcp = new TreeItem<>("tcp");
        TreeItem<String> udp = new TreeItem<>("udp");
        TreeItem<String> webSocket = new TreeItem<>("webSocket");
        root.getChildren().add(tcp);
        root.getChildren().add(udp);
        root.getChildren().add(webSocket);

        root.setExpanded(true);

        this.menuTree.setRoot(root);
        menuTree.setShowRoot(false);

    }
}
