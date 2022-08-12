package com.hqu.lly.protocol.websocket.server.initalizer;

import com.hqu.lly.protocol.websocket.server.handler.PathVariableHandler;
import com.hqu.lly.protocol.websocket.server.handler.WSChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSChannelInitializer extends ChannelInitializer<SocketChannel> {

//    @Autowired
//    private PathVariableHandler pathVariableHandler  ;



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
        ch.pipeline().addLast(new CorsHandler(corsConfig));
        //http 协议编解码器
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        //http请求聚合处理，多个HTTP请求或响应聚合为一个FullHtppRequest或FullHttpResponse
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        //大数据的分区传输
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        // 路径变量处理器
//        ch.pipeline().addLast("path-variable-handler",new PathVariableHandler());
        //websocket协议处理器
        ch.pipeline().addLast("websocket", new WebSocketServerProtocolHandler("/",null,true,65536*10));
        //自定义消息处理器

        ch.pipeline().addLast("msg",new WSChannelHandler());
    }
}
