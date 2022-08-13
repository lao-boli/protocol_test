package com.hqu.lly.protocol.udp.server;

import com.hqu.lly.common.BaseServer;
import com.hqu.lly.protocol.udp.server.handler.UDPServerHandler;
import com.hqu.lly.service.ChannelService;
import com.hqu.lly.service.UIService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

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

    private ChannelService channelService;

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
                    .handler(new UDPServerHandler(channelService));

             channel = bootstrap.bind(port).sync().channel();

             channel.closeFuture().addListener(new ChannelFutureListener() {
                 @Override
                 public void operationComplete(ChannelFuture channelFuture) throws Exception {

                     bossGroup.shutdownGracefully();
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
        log.info("udp server closed");

    }

    @Override
    public Channel call() throws Exception {
       init();
       log.info("udp server start successful at " + channel.localAddress());
       return channel;
    }

    @Override
    public void setPort(String port) {

        this.port = Integer.parseInt(port);

    }

    @Override
    public void setService(UIService uiService) {

        this.channelService = (ChannelService) uiService;

    }

    @Override
    public void sendMessage(String message, Channel udpChannel) {

        if (udpChannel instanceof UDPServerHandler.UDPChannel){

            InetSocketAddress clientAddr = (InetSocketAddress) udpChannel.remoteAddress();

            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), clientAddr));

            String formatSendMsg = MsgFormatUtil.formatSendMsg(message, clientAddr.toString());

            channelService.updateMsgList(formatSendMsg);

            log.info(formatSendMsg);
        }


    }
}
