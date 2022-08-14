package com.hqu.lly.view.controller;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.service.MessageService;
import com.hqu.lly.service.impl.ClientService;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ClientController implements Initializable {

    @FXML
    private TextField clientInput;

    @FXML
    private Button confirmButton;

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

    @Setter
    private BaseClient client;

    @Setter
    private String protocol;

    ObservableList<String> items = FXCollections.observableArrayList() ;

    @FXML
    void confirmAddr(MouseEvent event) {

        URI uri = URI.create(protocol + clientInput.getText());

        client.setURI(uri);

        client.setService(new ClientService() {
            @Override
            public void onError(Throwable e , String errorMessage) {

                Platform.runLater(() -> {

                    if (errorMessage != null) {

                        errorMsgLabel.setText(errorMessage);

                    }else{

                        errorMsgLabel.setText(e.getMessage());
                    }

                });
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

                Platform.runLater(() ->{
                    clientInput.setDisable(true);
                    confirmButton.setDisable(true);
                    disconnectButton.setDisable(false);
                    sendMsgButton.setDisable(false);
                });
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void disconnect(MouseEvent event) {

        client.destroy();

        Platform.runLater(() ->{
            clientInput.setDisable(false);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        errorMsgLabel.setTextFill(Color.rgb(198,85,81));
    }
}
