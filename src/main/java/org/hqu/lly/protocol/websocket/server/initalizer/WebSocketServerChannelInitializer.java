package org.hqu.lly.protocol.websocket.server.initalizer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Setter;
import org.hqu.lly.protocol.websocket.server.handler.WebSocketServerConnectHandler;
import org.hqu.lly.protocol.websocket.server.handler.WebSocketServerExceptionHandler;
import org.hqu.lly.protocol.websocket.server.handler.WebSocketServerMessageHandler;
import org.hqu.lly.service.impl.ConnectedServerService;


/**
 * <p>
 * WebSocket服务端初始化处理器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-09-21 19:43
 */
public class WebSocketServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Setter
    private ConnectedServerService serverService;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("HttpServerCodec", new HttpServerCodec());
        ch.pipeline().addLast("HttpObjectAggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        ch.pipeline().addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/", null, true, 65536 * 10));
        ch.pipeline().addLast("WebSocketServerConnectHandler", new WebSocketServerConnectHandler(serverService));
        ch.pipeline().addLast("WebSocketServerMessageHandler", new WebSocketServerMessageHandler(serverService));
        ch.pipeline().addLast("WebSocketServerExceptionHandler", new WebSocketServerExceptionHandler());
    }

}
