package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.view.controller.TCPServerController;

/**
 * <p>
 * TCP服务端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class TCPServerTabFactory extends BaseServerTabFactory<TCPServerController> implements TabFactory {

    public TCPServerTabFactory(){
        super.tabName = "server";
        super.tabPanePath = ResLocConsts.TCP_SERVER_PANE;
    }

}
