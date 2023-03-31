package org.hqu.lly.protocol.BaseHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ConnectedServerService;

/**
 * <p>
 * 基本服务端管理Channel连接的处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-09-18 11:13
 */
@Slf4j
public class BaseServerConnectHandler extends ChannelInboundHandlerAdapter {

    protected ConnectedServerService serverService;

    public BaseServerConnectHandler(ConnectedServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info(ctx.name() + " : server: {} connect with client: {}", channel.localAddress(), channel.remoteAddress());
        serverService.updateMsgList(new MsgLabel("连接成功,客户端地址为: " + channel.remoteAddress().toString()));
        serverService.addChannel(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info(ctx.name() + " : server: {} disconnect form client: {}", channel.localAddress(), channel.remoteAddress());
        serverService.updateMsgList(new MsgLabel("与客户端断开连接, 客户端地址: " + channel.remoteAddress().toString()));
        serverService.removeChannel(channel);
    }

}
