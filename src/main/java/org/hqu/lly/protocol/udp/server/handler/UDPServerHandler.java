package org.hqu.lly.protocol.udp.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ConnectionlessServerService;

import java.net.InetSocketAddress;

/**
 * <p>
 * UDP服务端控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/12 20:08
 */
@Slf4j
public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {


    private ConnectionlessServerService serverService;

    public UDPServerHandler(ConnectionlessServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dpk) throws Exception {
        InetSocketAddress sender = dpk.sender();

        String receiveText = dpk.content().toString(CharsetUtil.UTF_8);
        serverService.updateMsgList(new MsgLabel(MsgLabel.Type.RECEIVE, sender.toString(), receiveText));

        ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(receiveText, CharsetUtil.UTF_8), sender));
        serverService.updateMsgList(new MsgLabel(MsgLabel.Type.SEND, sender.toString(), receiveText));

        serverService.addInetSocketAddress(sender);
    }

}
