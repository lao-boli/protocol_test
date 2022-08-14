package com.hqu.lly.protocol.tcp.server;

import com.hqu.lly.common.BaseServer;
import com.hqu.lly.protocol.tcp.server.handler.TCPMessageHandler;
import com.hqu.lly.service.ChannelService;
import com.hqu.lly.service.MessageService;
import com.hqu.lly.service.impl.ServerService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;

/**
 * <p>
 * TCP server
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/4 10:45
 * @Version 1.0
 */
@Slf4j
@Data
public class TCPServer extends BaseServer {

    private String port;

    private ServerService serverService;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private Channel channel;

    @Override
    public void init() {

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new TCPMessageHandler(serverService));

                }
            });
            channel = serverBootstrap.bind(Integer.parseInt(port)).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
            log.info("tcp server start successful at " + channel.localAddress());
        } catch (Exception e) {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            log.error("tcp server error", e);

            if (e instanceof BindException){

                serverService.onError(e,"该端口已被占用");

            }
        }
    }


    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }

    @Override
    public void sendMessage(String msg , Channel channel){

        channel.writeAndFlush(msg);

        String formatSendMsg = MsgFormatUtil.formatSendMsg(msg, channel.remoteAddress().toString());

        serverService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);
    }


    @Override
    public void destroy() {
        if (null == channel){
            return;
        }
        channel.close();
        log.info("tcp server closed");
    }

    @Override
    public void setService(ServerService serverService) {
       this.serverService =  serverService;
    }
}
