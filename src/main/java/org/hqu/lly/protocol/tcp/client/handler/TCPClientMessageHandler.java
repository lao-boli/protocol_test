package org.hqu.lly.protocol.tcp.client.handler;

import org.hqu.lly.service.MessageService;
import org.hqu.lly.utils.MsgFormatUtil;
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

    private MessageService messageService;

    public TCPClientMessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();

        String serverAddr = channel.remoteAddress().toString();

        log.info("有服务端建立连接 " + "服务端address: " + serverAddr);

        log.info("服务端channel Id:" + channel.id().toString());

        messageService.updateMsgList("服务端address: " + serverAddr);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {

        String serverAddr = ctx.channel().remoteAddress().toString();

        String receiveText = msg;

        String formattedReceiveText = MsgFormatUtil.formatReceiveMsg(receiveText, serverAddr);

        messageService.updateMsgList(formattedReceiveText);

        log.info(formattedReceiveText);
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
