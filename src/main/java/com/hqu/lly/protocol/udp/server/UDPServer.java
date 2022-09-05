package com.hqu.lly.protocol.udp.server;

import com.hqu.lly.domain.base.BaseServer;
import com.hqu.lly.protocol.udp.server.handler.UDPServerHandler;
import com.hqu.lly.service.impl.ServerService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;
import java.net.InetSocketAddress;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/12 19:53
 * @Version 1.0
 */
@Slf4j
public class UDPServer extends BaseServer {

    private int port;

    private ServerService serverService;

    private Channel channel;

    private EventLoopGroup bossGroup;

    @Override
    public void init() {

        bossGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UDPServerHandler(serverService));

             channel = bootstrap.bind(port).sync().channel();

             channel.closeFuture().addListener(new ChannelFutureListener() {
                 @Override
                 public void operationComplete(ChannelFuture channelFuture) throws Exception {

                     bossGroup.shutdownGracefully();
                 }
             });
            log.info("udp server start successful at " + channel.localAddress());
        } catch (Exception e) {

            if (e instanceof BindException){

                serverService.onError(e,"该端口已被占用");
            }
            log.error("udp server error", e);
        }
    }

    @Override
    public void destroy() {

        if (null == channel){
            return;
        }
        channel.close();
        log.info("udp server closed");

    }

    @Override
    public Channel call() throws Exception {
       init();
       return channel;
    }

    @Override
    public void setPort(String port) {

        this.port = Integer.parseInt(port);

    }

    @Override
    public void setService(ServerService serverService) {

        this.serverService = serverService;

    }

    @Override
    public void sendMessage(String message, Channel udpChannel) {

        if (udpChannel instanceof UDPServerHandler.UDPChannel){

            InetSocketAddress clientAddr = (InetSocketAddress) udpChannel.remoteAddress();

            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), clientAddr));

            String formatSendMsg = MsgFormatUtil.formatSendMsg(message, clientAddr.toString());

            serverService.updateMsgList(formatSendMsg);

            log.info(formatSendMsg);
        }


    }
}
