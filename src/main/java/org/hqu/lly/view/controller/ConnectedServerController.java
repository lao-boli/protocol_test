package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.utils.UIUtil;

/**
 * <p>
 * 有连接服务控制器基类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/10/3 14:38
 */
public abstract class ConnectedServerController extends BaseServerController<Channel> {

    @Override
    protected void setServerService() {
        serverService = new ConnectedServerService() {
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
            public void addChannel(Channel channel) {
                Platform.runLater(() -> {
                    clientList.add(channel);
                    clientListBox.setItems(clientList);
                });
            }

            @Override
            public void removeChannel(Channel channel) {
                Platform.runLater(() -> {
                    clientList.remove(channel);
                    clientListBox.setItems(clientList);
                });
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
        clientListBox.setCellFactory(channelListView -> new ChannelCellFactory());
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
