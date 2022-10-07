package org.hqu.lly.protocol.tcp.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * TCP客户端管理Channel连接的处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/17 08:44
 */
@Slf4j
public class TCPClientConnectHandler extends ChannelInboundHandlerAdapter {

    private ClientService clientService;

    public TCPClientConnectHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("client: {} connect to server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList("连接成功, 服务端地址为: " + channel.remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("client: {} disconnect form server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList("与服务端断开连接, 服务端地址: " + channel.remoteAddress().toString());
        clientService.onClose();
    }

}
