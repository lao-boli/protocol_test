package org.hqu.lly.view.controller;

import org.hqu.lly.protocol.udp.server.UDPServer;

/**
 * <p>
 * UDP服务端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 09:15
 */
public class UDPServerController extends ConnectionlessServerController{

    @Override
    protected void setServer() {
        server = new UDPServer();
    }

}
