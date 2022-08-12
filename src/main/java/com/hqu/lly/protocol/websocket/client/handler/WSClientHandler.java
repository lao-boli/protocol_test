package com.hqu.lly.protocol.websocket.client.handler;

import com.hqu.lly.protocol.websocket.server.group.WSChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
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
 * @date 2022/8/11 20:06
 * @Version 1.0
 */
@Slf4j
public class WSClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("有服务端建立连接 " + "服务端address: " + channel.remoteAddress().toString());
        log.info("服务端channel Id:" + channel.id().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info(msg.text());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("服务端断开连接... 服务端 address: " + channel.remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info(channel.remoteAddress()+" 连接异常,断开连接...");
        cause.printStackTrace();
        ctx.channel().closeFuture();
    }
}
