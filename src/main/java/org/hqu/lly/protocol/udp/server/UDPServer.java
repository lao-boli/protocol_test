package org.hqu.lly.protocol.udp.server;

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
import org.hqu.lly.domain.bean.ConnectionlessServer;
import org.hqu.lly.protocol.udp.server.handler.UDPServerHandler;
import org.hqu.lly.service.impl.ConnectionlessServerService;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.MsgUtil;

import java.net.BindException;
import java.net.InetSocketAddress;

/**
 * <p>
 * UDP服务端
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/12 19:53
 */
@Slf4j
public class UDPServer extends ConnectionlessServer {

    private int port;

    private  ConnectionlessServerService serverService;

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
            channel.closeFuture().addListener(promise -> bossGroup.shutdownGracefully());
            log.info("udp server start successful at " + channel.localAddress());
        } catch (Exception e) {
            if (e instanceof BindException) {
                serverService.onError(e, "该端口已被占用");
            }
            log.error("udp server error", e);
        }
    }

    @Override
    public void destroy() {
        if (null == channel) {
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
        this.serverService = (ConnectionlessServerService) serverService;
    }


    @Override
    public void sendMessage(String message, InetSocketAddress dstAddr) {
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), dstAddr));
        String formatSendMsg = MsgUtil.formatSendMsg(message, dstAddr.toString());
        serverService.updateMsgList(formatSendMsg);
        log.info(formatSendMsg);
    }

}
