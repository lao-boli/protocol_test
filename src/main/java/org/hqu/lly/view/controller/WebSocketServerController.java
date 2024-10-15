package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.hqu.lly.domain.egg.Egg;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.websocket.server.WebSocketServer;
import org.hqu.lly.struct.Pair;
import org.hqu.lly.utils.ValidateUtil;

import java.util.concurrent.FutureTask;

/**
 * <p>
 * WebSocket服务端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:37
 */
public class WebSocketServerController extends ConnectedServerController{

    private String path;

    @FXML
    @Override
    void startServer(MouseEvent event) {
        Egg.egg(serverPort.getText());
        int port;
        String path;
        try {
            Pair<Integer, String> result = ValidateUtil.checkPortAndPath(serverPort.getText());
            port = result.getFirst();
            path = "/" + result.getSecond();
        } catch (IllegalArgumentException e) {
            errorMsgLabel.setText(e.getMessage());
            return;
        }
        assert server instanceof WebSocketServer;
        server.setPort(port);
        ((WebSocketServer) server).setPath(path);
        server.setService(serverService);

        FutureTask<Channel> serverTask = new FutureTask<>(server);
        executor.execute(serverTask);
        Platform.runLater(() -> errorMsgLabel.setText("服务开启中..."));
    }

    @Override
    protected void setServer() {
        server = new WebSocketServer();
        serverConfig.setPaneType(PaneType.WS_SERVER);
    }

}
