package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.WebSocketClientController;

/**
 * <p>
 * WebSocket客户端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class WSClientTabFactory extends BaseClientTabFactory<WebSocketClientController>{

    public WSClientTabFactory() {
        super.tabName = "client";
        super.tabPanePath = ResLoc.WEB_SOCKET_CLIENT_PANE;
    }

}
