package com.hqu.lly.protocol.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception {

        /**
         * 1、创建两个线程组 BossGroup 和 workerGroup
         * 2、bossGroup 只处理连接请求，workerGroup负责客户端业务处理
         * 3、两个都是无限循环
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来进行设置
            bootstrap
                    //设置两个线程组
                    .group(bossGroup, workerGroup)
                    //使用NioServerSocketChannel作为通道类型实现
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //给workerGroup的eventLoop线程对应的管道设置处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //初始化一个通道
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //通道上有很多处理器，都由pipeline管道统一管理
                            ChannelPipeline pipeline = ch.pipeline();
                            //管道里添加处理器
                        }
                    });
            System.out.println("server is prepare...");
            //绑定一个端口，并且同步，作用是启动服务器
            ChannelFuture cf = bootstrap.bind(6666).sync();
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }finally {
            //优雅的关闭线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
