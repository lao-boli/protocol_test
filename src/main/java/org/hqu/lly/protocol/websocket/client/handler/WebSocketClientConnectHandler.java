package org.hqu.lly.protocol.websocket.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.protocol.BaseHandler.BaseClientConnectHandler;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * WebSocket连接处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/20 19:18
 */
@Slf4j
public class WebSocketClientConnectHandler extends BaseClientConnectHandler {

    public WebSocketClientConnectHandler(ClientService clientService) {
        super(clientService);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("client: {} connect to server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList(new MsgLabel("连接到服务端: " + channel.remoteAddress().toString()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("client: {} disconnect form server: {}", channel.localAddress(), channel.remoteAddress());
        clientService.updateMsgList(new MsgLabel("与服务端断开连接, 服务端地址: " + channel.remoteAddress().toString()));
        clientService.onClose();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent) {
            WebSocketClientProtocolHandler.ClientHandshakeStateEvent event = (WebSocketClientProtocolHandler.ClientHandshakeStateEvent) evt;
            if (event.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED)) {
                clientService.updateMsgList(new MsgLabel("等待握手..."));
            }
            if (event.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
                clientService.updateMsgList(new MsgLabel("握手成功"));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
