package com.hqu.lly.protocol.websocket.server.handler;

import com.google.gson.*;
import com.hqu.lly.protocol.websocket.server.group.WSChannelGroup;
import com.hqu.lly.service.ChannelService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
//@ChannelHandler.Sharable
public class WSChannelHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
                    return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                }
            })
            .serializeNulls()
            .create();

    private ChannelService channelService;

    public WSChannelHandler() {
    }

    public WSChannelHandler(ChannelService channelService) {
        this.channelService = channelService;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        String clientAddr = channel.remoteAddress().toString();

        channelService.addChannel(channel);

        log.info("有客户端建立连接");
        log.info("客户端address: " + clientAddr);
        log.info("客户端channel Id:" + channel.id().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        String clientAddr = ctx.channel().remoteAddress().toString();

        String receiveText = msg.text();

        String formatReceiveMsg = MsgFormatUtil.formatReceiveMsg(receiveText, clientAddr);

        channelService.updateMsgList(formatReceiveMsg);

        String responseText = "your message is " + msg.text();

        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseText));

        String formatSendMsg = MsgFormatUtil.formatSendMsg(responseText, clientAddr);

        channelService.updateMsgList(formatSendMsg);

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
        channelService.removeChannel(channel);
        WSChannelGroup.channelGroup.remove(channel);
        String userId = WSChannelGroup.channelUserGroup.remove(channel);
//        WSChannelGroup.userChannelGroup.remove(userId, channel);
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
