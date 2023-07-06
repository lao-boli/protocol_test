package org.hqu.lly.view.controller;

import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.websocket.client.WebSocketClient;

/**
 * <p>
 * WebSocket客户端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:36
 */
public class WebSocketClientController extends BaseClientController<WebSocketClient>{

    @Override
    protected void setProtocol() {
        protocol = ProtocolConsts.WEB_SOCKET;
    }

    @Override
    protected void setClient() {
        client = new WebSocketClient();
        clientConfig.setPaneType(PaneType.WS_CLIENT);
    }

}
