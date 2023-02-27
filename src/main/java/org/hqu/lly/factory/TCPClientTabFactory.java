package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.TCPClientController;

/**
 * <p>
 * TCP客户端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class TCPClientTabFactory extends BaseClientTabFactory<TCPClientController> {

    public TCPClientTabFactory() {
        super.tabName = "client";
        super.tabPanePath = ResLoc.TCP_CLIENT_PANE;
    }

}
