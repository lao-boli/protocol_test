package org.hqu.lly.protocol.websocket.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgUtil;

/**
 * <p>
 * WebSocket客户端消息处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/11 20:06
 */
@Slf4j
public class WebSocketClientMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ClientService clientService;

    public WebSocketClientMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String serverAddr = ctx.channel().remoteAddress().toString();
        String receiveText = msg.text();
        String formattedReceiveText = MsgUtil.formatReceiveMsg(receiveText, serverAddr);
        clientService.updateMsgList(formattedReceiveText);
        log.info(formattedReceiveText);
    }

}
