package org.hqu.lly.protocol.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.protocol.websocket.client.handler.WebSocketClientConnectHandler;
import org.hqu.lly.protocol.websocket.client.handler.WebSocketClientExceptionHandler;
import org.hqu.lly.protocol.websocket.client.handler.WebSocketClientMessageHandler;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgUtil;

import java.net.ConnectException;
import java.net.URI;

/**
 * <p>
 * WebSocket客户端
 * <p>
 *
 * @author hqully
 * @version 1.1
 * @date 2022/8/10 20:36
 */
@Slf4j
public class WebSocketClient extends BaseClient {

    private String host;

    private int port;

    private URI uri;

    private ClientService clientService;

    private EventLoopGroup workerGroup;

    @Override
    public void init() {
        workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("http-codec", new HttpClientCodec());
                    ch.pipeline().addLast("aggregator", new HttpObjectAggregator(655360));
                    ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    ch.pipeline().addLast("WebSocketClientProtocolHandler", new WebSocketClientProtocolHandler(uri,
                                                                                                               WebSocketVersion.V13, "", true, getCustomHeaders(), Integer.MAX_VALUE));
                    ch.pipeline().addLast("WebSocketClientConnectHandler", new WebSocketClientConnectHandler(clientService));
                    ch.pipeline().addLast("WebSocketClientMessageHandler", new WebSocketClientMessageHandler(clientService));
                    ch.pipeline().addLast("WebSocketClientExceptionHandler", new WebSocketClientExceptionHandler(clientService));
                }
            });
            channel = b.connect(host, port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
            clientService.onStart();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            if (e.getCause() instanceof ConnectException) {
                log.warn(e.toString());
                clientService.onError(e, "该地址服务未开启");
            } else {
                log.error("webSocket client error", e);
                clientService.onError(e, "未知错误");
            }
        }
    }

    private DefaultHttpHeaders getCustomHeaders() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        // TODO custom headers if need
        return headers;
    }

    @Override
    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.close();
    }

    @Override
    public void setURI(URI uri) {
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.uri = uri;
    }

    @Override
    public void setService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);
        channel.writeAndFlush(new TextWebSocketFrame(message));
        clientService.updateMsgList(new MsgLabel(MsgUtil.formatSendMsg(message, channel.remoteAddress().toString())));
    }

    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }
}
