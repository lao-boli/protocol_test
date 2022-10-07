package org.hqu.lly.view.controller;

import org.hqu.lly.protocol.tcp.server.TCPServer;

/**
 * <p>
 * TCP服务端功能控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 09:15
 */
public class TCPServerController extends ConnectedServerController{


    @Override
    protected void setServer() {
        server = new TCPServer();
    }


}
