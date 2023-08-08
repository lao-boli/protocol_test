package org.hqu.lly.protocol.websocket.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ConnectedServerService;

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
        serverService.updateMsgList(new MsgLabel(MsgLabel.Type.RECEIVE, clientAddr, msg.text()));

        if (!serverService.isMuteReq()){
            ctx.channel().writeAndFlush(new TextWebSocketFrame(msg.text()));
            serverService.updateMsgList(new MsgLabel(MsgLabel.Type.SEND, clientAddr, msg.text()));
        }
    }

}
