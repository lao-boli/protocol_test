package org.hqu.lly.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.udp.client.UDPClient;

/**
 * <p>
 * UDP客户端控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 10:31
 */
@Slf4j
public class UDPClientController extends BaseClientController<UDPClient> {

    @Override
    protected void setProtocol() {
       protocol = ProtocolConsts.UDP;
    }

    @Override
    protected void setClient() {
        client = new UDPClient();
        clientConfig.setPaneType(PaneType.UDP_CLIENT);
    }

}
