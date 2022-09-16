package org.hqu.lly.protocol.websocket.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.websocket.server.group.WSChannelGroup;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.MsgUtil;

@Slf4j
public class WSServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private ServerService serverService;

    public WSServerHandler() {
    }

    public WSServerHandler(ServerService serverService) {
        this.serverService = serverService;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientAddr = channel.remoteAddress().toString();
        serverService.addChannel(channel);
        log.info("有客户端建立连接");
        log.info("客户端address: " + clientAddr);
        log.info("客户端channel Id:" + channel.id().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        String clientAddr = ctx.channel().remoteAddress().toString();

        String receiveText = msg.text();

        String formatReceiveMsg = MsgUtil.formatReceiveMsg(receiveText, clientAddr);

        serverService.updateMsgList(formatReceiveMsg);

        String responseText = "your message is " + msg.text();

        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseText));

        String formatSendMsg = MsgUtil.formatSendMsg(responseText, clientAddr);

        serverService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);

    }

    private void sendTo(String msg, String username) {
        Channel userChannel = WSChannelGroup.userChannelGroup.get(username);
        if (userChannel != null) {
            userChannel.writeAndFlush(new TextWebSocketFrame(msg));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientAddr = channel.remoteAddress().toString();
        log.info("客户端断开连接... 客户端 address: " + clientAddr);
        serverService.removeChannel(channel);
        WSChannelGroup.channelGroup.remove(channel);
        String userId = WSChannelGroup.channelUserGroup.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info(channel.remoteAddress() + " 连接异常,断开连接...");
        cause.printStackTrace();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端500 关闭连接"));
        ctx.channel().closeFuture();
        WSChannelGroup.channelGroup.remove(channel);
        WSChannelGroup.userChannelGroup.remove(channel.id().toString(), channel);
    }
}
