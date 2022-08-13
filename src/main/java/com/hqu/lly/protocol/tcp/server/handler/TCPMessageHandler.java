package com.hqu.lly.protocol.tcp.server.handler;

import com.hqu.lly.protocol.tcp.server.group.TCPChannelGroup;
import com.hqu.lly.service.ChannelService;
import com.hqu.lly.utils.MsgFormatUtil;
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
public class TCPMessageHandler extends SimpleChannelInboundHandler<String> {  // 6


    private ChannelService channelService;

    public TCPMessageHandler(ChannelService ChannelService) {
        this.channelService = ChannelService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("有客户端建立连接");
        log.info("客户端address: " + channel.remoteAddress().toString());
        log.info("客户端channel Id:" + channel.id().toString());
        channelService.updateMsgList("客户端address: " + channel.remoteAddress().toString());

        channelService.addChannel(channel);
        TCPChannelGroup.clientChannelGroup.put(channel.id().toString(), channel);


    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {


        String clientAddr = ctx.channel().remoteAddress().toString();

        String receiveText = msg;

        String formatReceiveMsg = MsgFormatUtil.formatReceiveMsg(receiveText, clientAddr);

        channelService.updateMsgList(formatReceiveMsg);

        String responseText = "your message is " + msg;

        ctx.channel().writeAndFlush(responseText);

        String formatSendMsg = MsgFormatUtil.formatSendMsg(responseText, clientAddr);

        channelService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        TCPChannelGroup.clientChannelGroup.remove(channel.id().toString());
        log.info("客户端断开连接... 客户端 address: " + channel.remoteAddress());
        channelService.updateMsgList("客户端断开连接... 客户端address: " + channel.remoteAddress().toString());
        channelService.removeChannel(channel);
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
