package com.hqu.lly.view.controller;

import com.hqu.lly.protocol.tcp.client.TCPClient;
import com.hqu.lly.service.UIService;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ClientController {

    @FXML
    private TextField clientInput;

    @FXML
    private Button confirmButton;

    @FXML
    private TextArea msgInput;

    @FXML
    private Button sendMsgButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private ListView<String> msgList;


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

    public void destroy(){
       tcpClient.destroy();
    }

}
