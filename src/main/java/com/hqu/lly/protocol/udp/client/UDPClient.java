package com.hqu.lly.protocol.udp.client;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.protocol.udp.client.handler.UDPClientHandler;
import com.hqu.lly.service.MessageService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URI;

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
public class UDPClient extends BaseClient {

    private int port;

    private String host;

    private MessageService messageService;

    private Channel channel;

    private EventLoopGroup eventLoopGroup;

    private InetSocketAddress serverAddr;

    @Override
    public void init() {

        eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UDPClientHandler(messageService));

            channel = bootstrap.connect(host, port).sync().channel();

            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    eventLoopGroup.shutdownGracefully();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

        if (null == channel) {
            return;
        }
        channel.close();
        log.info("udp client closed");

    }

    @Override
    public void setURI(URI uri) {
        this.host = uri.getHost();
        this.port = uri.getPort();
        serverAddr = new InetSocketAddress(host, port);

    }

    @Override
    public void setService(MessageService messageService) {
        this.messageService = messageService;

    }

    @Override
    public void sendMessage(String message) {

        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), serverAddr));

        messageService.updateMsgList(MsgFormatUtil.formatSendMsg(message,serverAddr.toString()));

    }

    @Override
    public Channel call() throws Exception {
        init();
        log.info("udp server start successful at " + channel.localAddress());
        return channel;
    }


}
