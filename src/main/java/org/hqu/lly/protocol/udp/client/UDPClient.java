package org.hqu.lly.protocol.udp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.protocol.udp.client.handler.UDPClientHandler;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgUtil;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * <p>
 * UDP客户端
 * <p>
 *
 * @author hqully
 * @date 2022/8/12 19:53
 * @version 1.0
 */
@Slf4j
public class UDPClient extends BaseClient {

    private int port;

    private String host;

    private ClientService clientService;

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
                    .handler(new UDPClientHandler(clientService));
            channel = bootstrap.bind(0).sync().channel();
            channel.closeFuture().addListener(promise -> eventLoopGroup.shutdownGracefully());
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
    public void setService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void sendMessage(String message) {
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), serverAddr));
        clientService.updateMsgList(MsgUtil.formatSendMsg(message, serverAddr.toString()));
    }

    @Override
    public Channel call() throws Exception {
        init();
        log.info("udp client start successful at " + channel.localAddress());
        return channel;
    }

}
