package org.hqu.lly.protocol.BaseHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * 基本客户端管理Channel连接的处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-09-18 11:12
 */
@Slf4j
public class BaseClientConnectHandler extends ChannelInboundHandlerAdapter {

    protected ClientService clientService;

    public BaseClientConnectHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info(ctx.name() + " : client: {} connect to server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList(new MsgLabel("连接成功, 服务端地址为: " + channel.remoteAddress().toString()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info(ctx.name() + " : client: {} disconnect form server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList(new MsgLabel("与服务端断开连接, 服务端地址: " + channel.remoteAddress().toString()));
        channel.close();
        clientService.onClose();
    }

}
