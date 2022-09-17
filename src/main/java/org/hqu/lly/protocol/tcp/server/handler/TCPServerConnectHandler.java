package org.hqu.lly.protocol.tcp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.tcp.server.group.TCPChannelGroup;
import org.hqu.lly.service.impl.ServerService;

/**
 * <p>
 * 管理TCPChannel的连接
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/16 20:04
 */
@Slf4j
public class TCPServerConnectHandler extends ChannelInboundHandlerAdapter {

    private ServerService serverService;

    public TCPServerConnectHandler(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("connect success, client address: {}", channel.remoteAddress());
        serverService.updateMsgList("连接成功,客户端地址为: " + channel.remoteAddress().toString());
        serverService.addChannel(channel);
        TCPChannelGroup.clientChannelGroup.put(channel.id().toString(), channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        TCPChannelGroup.clientChannelGroup.remove(channel.id().toString());
        log.info("disconnect form client: {}", channel.remoteAddress());
        serverService.updateMsgList("与客户端断开连接, 客户端地址: " + channel.remoteAddress().toString());
        serverService.removeChannel(channel);
    }
}
