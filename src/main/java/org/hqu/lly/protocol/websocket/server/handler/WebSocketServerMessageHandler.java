package org.hqu.lly.protocol.websocket.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.utils.MsgUtil;

/**
 * <p>
 * WebSocket服务端消息处理器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-09-18 14:48
 */
@Slf4j
public class WebSocketServerMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ConnectedServerService serverService;

    public WebSocketServerMessageHandler(ConnectedServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String clientAddr = ctx.channel().remoteAddress().toString();
        String receiveText = msg.text();
        String formatReceiveMsg = MsgUtil.formatReceiveMsg(receiveText, clientAddr);
        serverService.updateMsgList(formatReceiveMsg);
        log.info(formatReceiveMsg);

        String responseText = "your message is " + msg.text();
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseText));
        String formatSendMsg = MsgUtil.formatSendMsg(responseText, clientAddr);
        serverService.updateMsgList(formatSendMsg);
        log.info(formatSendMsg);
    }

}
