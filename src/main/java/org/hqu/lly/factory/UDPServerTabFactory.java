package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.view.controller.UDPServerController;

/**
 * <p>
 * TCP服务端标签页工厂
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/7 14:57
 * @Version 1.0
 */
public class UDPServerTabFactory extends BaseServerTabFactory<UDPServerController> implements TabFactory {

    public UDPServerTabFactory() {
        super.tabName = "server";
        super.tabPanePath = ResLocConsts.UDP_SERVER_PANE;
    }

}
