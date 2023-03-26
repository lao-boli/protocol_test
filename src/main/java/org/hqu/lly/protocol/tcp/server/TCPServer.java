package org.hqu.lly.protocol.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.bean.ConnectedServer;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.protocol.BaseHandler.BaseServerConnectHandler;
import org.hqu.lly.protocol.tcp.codec.LTCEncoder;
import org.hqu.lly.protocol.tcp.codec.MessageDecoderSelector;
import org.hqu.lly.protocol.tcp.codec.MessageEncoderSelector;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;
import org.hqu.lly.protocol.tcp.server.handler.TCPServerExceptionHandler;
import org.hqu.lly.protocol.tcp.server.handler.TCPServerMessageHandler;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.MsgUtil;

import java.net.BindException;

/**
 * <p>
 * TCP server
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/4 10:45
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class TCPServer extends ConnectedServer {

    private int port;

    private ConnectedServerService serverService;

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
                    ch.pipeline().addLast("TCPServerConnectHandler", new BaseServerConnectHandler(serverService));
                    ch.pipeline().addLast("MessageDecoderSelector", new MessageDecoderSelector());
                    ch.pipeline().addLast("LTCDecoder", new LengthFieldBasedFrameDecoder(1024 * 100, 4, 4, 0, 8));
                    ch.pipeline().addLast("StringDecoder", new StringDecoder());
                    ch.pipeline().addLast("StringEncoder", new StringEncoder());
                    ch.pipeline().addLast("LTCEncoder", new LTCEncoder());
                    ch.pipeline().addLast("MessageEncoderSelector", new MessageEncoderSelector());
                    ch.pipeline().addLast("TCPServerMessageHandler", new TCPServerMessageHandler(serverService));
                    ch.pipeline().addLast("TCPExceptionHandler", new TCPServerExceptionHandler());
                }
            });

            channel = serverBootstrap.bind(port).sync().channel();
            AppChannelGroup.TCPServerChannelSet.add("/127.0.0.1:" + port);

            channel.closeFuture().addListener(promise -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                AppChannelGroup.TCPServerChannelSet.remove("/127.0.0.1:" + port);
                log.info("tcp server closed");
            });
            log.info("tcp server start successful at " + channel.localAddress());
            serverService.onStart();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            if (e instanceof BindException) {
                log.warn(e.toString());
                serverService.onError(e, "该端口已被占用");
            } else {
                log.error("tcp server error", e);
            }
        }
    }


    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }

    @Override
    public void sendMessage(String msg, Channel channel) {
        channel.writeAndFlush(msg);
        String formatSendMsg = MsgUtil.formatSendMsg(msg, channel.remoteAddress().toString());
        serverService.updateMsgList(new MsgLabel(formatSendMsg));
        log.info(formatSendMsg);
    }


    @Override
    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.close();
    }

    @Override
    public void setService(ServerService serverService) {
        this.serverService = (ConnectedServerService) serverService;
    }
}
