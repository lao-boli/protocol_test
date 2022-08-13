package com.hqu.lly.view.controller;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.service.MessageService;
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
import lombok.Setter;

import java.net.URI;
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

    @Setter
    private BaseClient client;

    @Setter
    private String protocol;

    ObservableList<String> items = FXCollections.observableArrayList() ;

    @FXML
    void confirmAddr(MouseEvent event) {

        URI uri = URI.create(protocol + clientInput.getText());

        client.setURI(uri);

        client.setService(new MessageService() {
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

        client.destroy();

        Platform.runLater(() ->{
            confirmButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
        });
    }

    @FXML
    void sendMsg(MouseEvent event) {
        client.sendMessage(msgInput.getText());

    }

    public void destroy(){
       client.destroy();
    }

}
