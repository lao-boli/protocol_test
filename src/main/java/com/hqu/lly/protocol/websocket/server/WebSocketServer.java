package com.hqu.lly.protocol.websocket.server;

import com.hqu.lly.protocol.websocket.initalizer.WSChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * <p>
 * websocket服务类
 * <p>
 *
 * @author liulingyu
 * @date 2022/7/2 10:59
 * @version 1.0
 */
@Data
@Slf4j
public class WebSocketServer {

    private int port;
    private String host;
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private WSChannelInitializer wsChannelInitializer = new WSChannelInitializer();
    public void init(){
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //指定服务端连接队列长度，也就是服务端处理线程全部繁忙，并且队列长度已达到1024个，后续请求将会拒绝
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(wsChannelInitializer);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            log.info("Netty websocket start successful at " + f.channel().localAddress());
             f.channel().closeFuture().sync().channel();
        } catch (Exception e) {
            log.error("WS服务启动失败：" + e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
    public void run(String... args) {
        init();
    }
}
