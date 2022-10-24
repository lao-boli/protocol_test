package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.hqu.lly.service.impl.ConnectionlessServerService;
import org.hqu.lly.utils.UIUtil;

import java.net.InetSocketAddress;

/**
 * <p>
 * 无连接服务端基本控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/10/3 15:49
 */
public abstract class ConnectionlessServerController extends BaseServerController<InetSocketAddress> {

    @Override
    protected void setServerService() {
        serverService = new ConnectionlessServerService() {
            @Override
            public void addInetSocketAddress(InetSocketAddress dstAddr) {
                Platform.runLater(() -> {
                    clientList.add(dstAddr);
                    clientListBox.setItems(clientList);
                });
            }

            @Override
            public void removeInetSocketAddress(InetSocketAddress dstAddr) {
                Platform.runLater(() -> {
                    clientList.remove(dstAddr);
                    clientListBox.setItems(clientList);
                });

            }

            @Override
            public void onStart() {
                if (!errorMsgLabel.getText().isEmpty()) {
                    Platform.runLater(() -> {
                        errorMsgLabel.setText("");
                    });
                }
                setActiveUI();
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
        };

    }

    @Override
    protected void setClientBoxCellFactory() {
        // 自定义细胞工厂，设置显示的内容
        clientListBox.setCellFactory(channelListView -> new InetSocketAddressCellFactory());
        // 点击时将当前的clientAddr设置为选中的dstAddr
        clientListBox.getSelectionModel().selectedItemProperty().addListener((observableValue, preChannel, currentChannel) -> targetClient = currentChannel);
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
