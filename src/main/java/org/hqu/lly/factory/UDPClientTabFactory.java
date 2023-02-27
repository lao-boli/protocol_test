package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.UDPClientController;

/**
 * <p>
 * UDP客户端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class UDPClientTabFactory extends BaseClientTabFactory<UDPClientController>{

    private String tabName = "client";


    public UDPClientTabFactory() {
        super.tabName = "client";
        super.tabPanePath = ResLoc.UDP_CLIENT_PANE;
    }

}
