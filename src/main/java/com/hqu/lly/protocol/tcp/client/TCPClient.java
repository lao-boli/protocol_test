package com.hqu.lly.protocol.tcp.client;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.protocol.tcp.client.handler.TCPClientMessageHandler;
import com.hqu.lly.service.UIService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * <p>
 * TCP client
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/4 10:46
 * @Version 1.0
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCPClient extends BaseClient {

    private Channel channel;

    private String host;

    private String port;

    private UIService uiService;


    private EventLoopGroup eventLoopGroup ;

    @Override
    public void sendMessage(String message){
        channel.writeAndFlush(message);
    }


    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }

    @Override
    public void init() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        this.eventLoopGroup = eventLoopGroup;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new TCPClientMessageHandler(uiService));

                        }
                    });
            //绑定服务器
            ChannelFuture channelFuture = bootstrap.connect(host, Integer.parseInt(port)).sync();
            this.channel = channelFuture.channel();
            this.channel.closeFuture().addListener((ChannelFutureListener) future -> {
                log.debug("处理关闭之后的操作");
                eventLoopGroup.shutdownGracefully();
            });
        } catch (Exception e) {
            e.printStackTrace();
            eventLoopGroup.shutdownGracefully();
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
    public void setAddress(String host, String port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void setService(UIService uiService) {
        this.uiService = uiService;
    }

}
