package org.hqu.lly.view.controller;

import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.websocket.server.WebSocketServer;

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

    @Override
    protected void setServer() {
        server = new WebSocketServer();
        serverConfig.setPaneType(PaneType.WS_SERVER);
    }

}
