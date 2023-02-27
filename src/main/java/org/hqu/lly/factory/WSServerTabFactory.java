package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.WebSocketServerController;

/**
 * <p>
 * WebSocket服务端标签页工厂
 * <p>
 *
 * @author hqully
 * @date 2022/8/7 14:57
 * @version 1.0
 */
public class WSServerTabFactory extends BaseServerTabFactory<WebSocketServerController>{

    public WSServerTabFactory(){
        super.tabName = "server";
        super.tabPanePath = ResLoc.WEB_SOCKET_SERVER_PANE;
    }

}
