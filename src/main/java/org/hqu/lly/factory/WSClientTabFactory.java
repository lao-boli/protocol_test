package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLocConsts;
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
public class WSClientTabFactory extends BaseClientTabFactory<WebSocketClientController> implements TabFactory {

    public WSClientTabFactory() {
        super.tabName = "client";
        super.tabPanePath = ResLocConsts.WEB_SOCKET_CLIENT_PANE;
    }

}
