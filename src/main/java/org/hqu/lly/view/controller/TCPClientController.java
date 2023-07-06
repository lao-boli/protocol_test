package org.hqu.lly.view.controller;

import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.tcp.client.TCPClient;

/**
 * <p>
 * TCP客户端功能控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:25
 */
public class TCPClientController extends BaseClientController<TCPClient> {

    @Override
    protected void setProtocol() {
        protocol = ProtocolConsts.TCP;
    }

    @Override
    protected void setClient() {
        client = new TCPClient();
        clientConfig.setPaneType(PaneType.TCP_CLIENT);
    }

}
