package com.hqu.lly.protocol.tcp.client;

import com.hqu.lly.protocol.tcp.client.handler.TCPClientMessageHandler;
import com.hqu.lly.protocol.tcp.server.handler.TCPMessageHandler;
import com.hqu.lly.service.UIService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

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
public class TCPClient implements Callable<Channel>  {

    private Channel channel;

    private String host;

    private String port;

    private UIService uiService;


    private EventLoopGroup eventLoopGroup ;

    public void sendMessage(String message){
        channel.writeAndFlush(message);
    }


    @Override
    public Channel call() throws Exception {

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
            this.channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.debug("处理关闭之后的操作");
                    eventLoopGroup.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            eventLoopGroup.shutdownGracefully();
        }
        return channel;
    }

    public void destroy() {
        if (null == channel){
           return;
        }
        channel.close();
    }

}
