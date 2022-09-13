package org.hqu.lly.protocol.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
import org.hqu.lly.domain.base.BaseServer;
import org.hqu.lly.protocol.tcp.codec.LTCEncoder;
import org.hqu.lly.protocol.tcp.codec.MessageDecoderSelector;
import org.hqu.lly.protocol.tcp.codec.MessageEncoderSelector;
import org.hqu.lly.protocol.tcp.constant.CommonConsts;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;
import org.hqu.lly.protocol.tcp.server.group.TCPChannelGroup;
import org.hqu.lly.protocol.tcp.server.handler.TCPMessageHandler;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.MsgFormatUtil;

import java.net.BindException;

/**
 * <p>
 * TCP server
 * <p>
 *
 * @author liulingyu
 * @version 1.0
 * @date 2022/8/4 10:45
 */
@EqualsAndHashCode(callSuper = true)
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
                    ch.pipeline().addLast("MessageDecoderSelector",new MessageDecoderSelector());
                    ch.pipeline().addLast("LTCDecoder",new LengthFieldBasedFrameDecoder(1024*100,4,4,0,8));
                    ch.pipeline().addLast("StringDecoder",new StringDecoder());
                    ch.pipeline().addLast("StringEncoder",new StringEncoder());
                    ch.pipeline().addLast("LTCEncoder",new LTCEncoder());
                    ch.pipeline().addLast("MessageEncoderSelector",new MessageEncoderSelector(CommonConsts.SERVER));
                    ch.pipeline().addLast("MessageHandler",new TCPMessageHandler(serverService));

                }
            });
            channel = serverBootstrap.bind(Integer.parseInt(port)).sync().channel();
            ChannelFuture f = channel.closeFuture();
            f.addListener(promise -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
            AppChannelGroup.serverChannelSet.add("/127.0.0.1:"+port);
            log.info("tcp server start successful at " + channel.localAddress());
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

        String formatSendMsg = MsgFormatUtil.formatSendMsg(msg, channel.remoteAddress().toString());

        serverService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);
    }


    @Override
    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.close();
        log.info("tcp server closed");
    }

    @Override
    public void setService(ServerService serverService) {
        this.serverService = serverService;
    }
}
