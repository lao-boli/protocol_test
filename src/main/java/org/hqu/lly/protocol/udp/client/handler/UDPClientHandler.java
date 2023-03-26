package org.hqu.lly.protocol.udp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * UDP客户端消息处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/13 9:22
 */
public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private ClientService clientService;

    public UDPClientHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String receiveText = datagramPacket.content().toString(CharsetUtil.UTF_8);
        String serverAddr = datagramPacket.sender().toString();
        clientService.updateMsgList(new MsgLabel(MsgLabel.Type.RECEIVE, serverAddr, receiveText));
    }

}
