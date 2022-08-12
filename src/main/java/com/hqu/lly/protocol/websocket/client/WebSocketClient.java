package com.hqu.lly.protocol.websocket.client;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.protocol.websocket.client.handler.WSClientHandler;
import com.hqu.lly.service.UIService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/10 20:36
 * @Version 1.0
 */
public class WebSocketClient extends BaseClient {

    private String host;

    private int port;

    private URI uri;

    private UIService uiService;
    private Channel channel;
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
                    //处理http请求的编解码器
                    ch.pipeline().addLast("http-codec", new HttpClientCodec());
                    ch.pipeline().addLast("aggregator", new HttpObjectAggregator(655360));
                    ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    ch.pipeline().addLast("ws", new WebSocketClientProtocolHandler(uri,
                            WebSocketVersion.V13, "", true, new DefaultHttpHeaders(), Integer.MAX_VALUE));
                    ch.pipeline().addLast("handler", new WSClientHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    workerGroup.shutdownGracefully();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

        if (null == channel){
            return;
        }
        channel.close();
    }

    @Override
    public void setURI(URI uri) {
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.uri = uri;



    }


    @Override
    public void setService(UIService uiService) {
        this.uiService = uiService;
    }

    @Override
    public void sendMessage(String message) {

        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }
}
