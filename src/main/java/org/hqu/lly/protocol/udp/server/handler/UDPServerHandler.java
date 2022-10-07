package org.hqu.lly.protocol.udp.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ConnectionlessServerService;
import org.hqu.lly.utils.MsgUtil;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * UDP服务端控制器
 * <p>
 *
 * @author hqully
 * @date 2022/8/12 20:08
 * @version 1.0
 */
@Slf4j
public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Set<InetSocketAddress> clientAddrSet= ConcurrentHashMap.newKeySet();

    private ConnectionlessServerService serverService;

    public UDPServerHandler(ConnectionlessServerService serverService) {
        this.serverService = serverService;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dpk) throws Exception {
        InetSocketAddress sender = dpk.sender();
        String clientAddr = sender.toString();

        String receiveText = dpk.content().toString(CharsetUtil.UTF_8);
        String formatReceiveMsg = MsgUtil.formatReceiveMsg(receiveText, clientAddr);
        serverService.updateMsgList(formatReceiveMsg);

        String responseText = "your message is " + receiveText;
        String formatSendMsg = MsgUtil.formatSendMsg(responseText, clientAddr);
        ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseText, CharsetUtil.UTF_8), sender));
        serverService.updateMsgList(formatSendMsg);

        if (!clientAddrSet.contains(sender)){
            serverService.addInetSocketAddress(sender);
            clientAddrSet.add(sender);
        }
    }

}
