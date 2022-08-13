package com.hqu.lly.protocol.udp.client.handler;

import com.hqu.lly.service.UIService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/13 9:22
 * @Version 1.0
 */
public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private UIService uiService;

    public UDPClientHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

        String receiveText = datagramPacket.content().toString(CharsetUtil.UTF_8);

        String serverAddr = datagramPacket.sender().toString();

        String formatReceiveMsg = MsgFormatUtil.formatReceiveMsg(receiveText, serverAddr);

        uiService.updateMsgList(formatReceiveMsg);

    }
}
