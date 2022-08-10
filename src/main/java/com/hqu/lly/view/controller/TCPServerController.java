package com.hqu.lly.view.controller;

import com.hqu.lly.protocol.tcp.server.TCPServer;
import com.hqu.lly.service.ChannelService;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class TCPServerController implements Initializable {

    @FXML
    private TextField serverPort;

    @FXML
    private Button confirmButton;

    @FXML
    private TextArea msgInput;

    @FXML
    private Button sendMsgButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private ListView<String> msgListBox;

    @FXML
    private ListView<Channel> clientListBox;

    private TCPServer tcpServer = new TCPServer();

    private Channel targetClientChannel = null;


    ObservableList<String> msgList = FXCollections.observableArrayList() ;

    ObservableList<Channel> channelList = FXCollections.observableArrayList() ;

    @FXML
    void closeServer(MouseEvent event) {
        tcpServer.destroy();
    }


    @FXML
    void startServer(MouseEvent event) {

        String port = serverPort.getText();

        tcpServer.setPort(port);

        tcpServer.setChannelService(new ChannelService() {

            @Override
            public void addChannel(Channel channel) {

                Platform.runLater(() -> {

                    channelList.add(channel);
                    clientListBox.setItems(channelList);
                });
            }

            @Override
            public void removeChannel(Channel channel) {

                channelList.remove(channel);
                clientListBox.setItems(channelList);
            }

            @Override
            public void updateMsgList(String msg) {

                Platform.runLater(() -> {

                    msgList.add(msg);

                    msgListBox.setItems(msgList);
                });
            }
        });

        FutureTask<Channel> channel = new FutureTask<Channel>(tcpServer);
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
    void sendMsg(MouseEvent event) {

        if (targetClientChannel != null) {
            tcpServer.sendMessage(msgInput.getText(),targetClientChannel);
        }

    }

    public void destroy(){
        tcpServer.destroy();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clientListBox.setCellFactory(new Callback<ListView<Channel>, ListCell<Channel>>() {
            @Override
            public ListCell<Channel> call(ListView<Channel> channelListView) {
                return new ChannelCellFactory();
            }
        });
        clientListBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Channel>() {
            @Override
            public void changed(ObservableValue<? extends Channel> observableValue, Channel preChannel, Channel currentChannel) {
                targetClientChannel = currentChannel;
            }
        });


    }

    class ChannelCellFactory extends ListCell<Channel> {
        @Override
        protected void updateItem(Channel channel, boolean empty) {
            super.updateItem(channel, empty);
            if (channel != null){

                setText(channel.remoteAddress().toString());
            }else {
                setText(null);
            }

        }
    }


}
