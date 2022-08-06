package com.hqu.lly.protocol.tcp.client.handler;

import com.hqu.lly.service.UIService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/4 19:55
 * @Version 1.0
 */
@Slf4j
public class TCPClientMessageHandler extends SimpleChannelInboundHandler<String> {  // 6

    private UIService uiService;

    public TCPClientMessageHandler(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("===========pathHandle================");
        log.info("连接到服务器");
        log.info("服务端address: " + channel.remoteAddress().toString());
        log.info("服务端channel Id:" + channel.id().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg);
        System.out.println(ctx.channel().remoteAddress());
        uiService.updateMsgList(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("服务端断开连接... 服务端 address: " + channel.remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info(channel.remoteAddress() + " 连接异常,断开连接...");
        cause.printStackTrace();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端500 关闭连接"));
        ctx.channel().closeFuture();
    }
}
