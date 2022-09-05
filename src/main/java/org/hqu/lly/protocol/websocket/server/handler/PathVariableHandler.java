package org.hqu.lly.protocol.websocket.server.handler;

import org.hqu.lly.protocol.websocket.server.group.WSChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *     处理websocket路径参数的处理器
 * </p>
 * @author liulingyu
 * @date 2022-07-03 10:50
 * @version 1.0
 */
@Slf4j
@Data
public class PathVariableHandler extends ChannelInboundHandlerAdapter {

    private String wsUri = "/";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("===========pathHandle================");
        log.info("有客户端建立连接");
        log.info("客户端address: " + channel.remoteAddress().toString());
        log.info("客户端channel Id:" + channel.id().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        if (null != msg && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            String userId = uri.substring(uri.lastIndexOf("/") + 1);
            log.info(userId);
            request.setUri(wsUri);

            if (WSChannelGroup.userChannelGroup.containsKey(userId)){

                Channel prevChannel = WSChannelGroup.userChannelGroup.get(userId);

                prevChannel.close();

                WSChannelGroup.channelGroup.removeIf(channel -> channel.equals(prevChannel) );
            }
            WSChannelGroup.channelGroup.add(ctx.channel());
            WSChannelGroup.channelUserGroup.put(ctx.channel(), userId);
            WSChannelGroup.userChannelGroup.put(userId,ctx.channel());
        }
        ctx.fireChannelRead(msg);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("客户端断开连接... 客户端 address: " + channel.remoteAddress());
        WSChannelGroup.channelGroup.remove(channel);
        String userId = WSChannelGroup.channelUserGroup.remove(channel);
        WSChannelGroup.userChannelGroup.remove(userId, channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info(channel.remoteAddress()+" 连接异常,断开连接...");
        cause.printStackTrace();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端500 关闭连接"));
        ctx.channel().closeFuture();
        WSChannelGroup.channelGroup.remove(channel);
        String userId = WSChannelGroup.channelUserGroup.remove(channel);
        WSChannelGroup.userChannelGroup.remove(userId, channel);
    }
}
