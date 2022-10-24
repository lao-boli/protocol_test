package org.hqu.lly.protocol.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.protocol.BaseHandler.BaseClientConnectHandler;
import org.hqu.lly.protocol.tcp.client.handler.TCPClientExceptionHandler;
import org.hqu.lly.protocol.tcp.client.handler.TCPClientMessageHandler;
import org.hqu.lly.protocol.tcp.codec.LTCEncoder;
import org.hqu.lly.protocol.tcp.codec.MessageDecoderSelector;
import org.hqu.lly.protocol.tcp.codec.MessageEncoderSelector;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgUtil;

import java.net.ConnectException;
import java.net.URI;

/**
 * <p>
 * TCP client
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/4 10:46
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class TCPClient extends BaseClient {

    private Channel channel;

    private String host;

    private int port;

    private ClientService clientService;

    private EventLoopGroup eventLoopGroup;

    @Override
    public void sendMessage(String message) {
        channel.writeAndFlush(message);
        String formattedText = MsgUtil.formatSendMsg(message, channel.remoteAddress().toString());
        log.info(formattedText);
        clientService.updateMsgList(formattedText);
    }


    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }

    @Override
    public void init() {

        this.eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("TCPClientConnectHandler", new BaseClientConnectHandler(clientService));
                            ch.pipeline().addLast("MessageDecoderSelector", new MessageDecoderSelector());
                            ch.pipeline().addLast("LTCDecoder", new LengthFieldBasedFrameDecoder(1024 * 100, 4, 4, 0, 8));
                            ch.pipeline().addLast("StringDecoder", new StringDecoder());
                            ch.pipeline().addLast("StringEncoder", new StringEncoder());
                            ch.pipeline().addLast("LTCEncoder", new LTCEncoder());
                            ch.pipeline().addLast("MessageEncoderSelector", new MessageEncoderSelector());
                            ch.pipeline().addLast("TCPClientMessageHandler", new TCPClientMessageHandler(clientService));
                            ch.pipeline().addLast("TCPClientExceptionHandler", new TCPClientExceptionHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel = channelFuture.channel();
            channel.closeFuture().addListener(promise -> {
                AppChannelGroup.TCPClientChannelSet.remove(channel.localAddress().toString());
                eventLoopGroup.shutdownGracefully();
            });
            AppChannelGroup.TCPClientChannelSet.add(channel.localAddress().toString());

            // 如果通道开启,则通知UI线程更新为开启状态的UI
            if (channel.isActive()){
                clientService.onStart();
            }
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();
            if (e.getCause() instanceof ConnectException) {
                log.warn(e.toString());
                clientService.onError(e, "该地址服务未开启");
            } else {
                log.error("tcp client error", e);
                clientService.onError(e, "未知错误");
            }
        }
    }

    @Override
    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.close();
    }

    @Override
    public void setURI(URI uri) {
        this.host = uri.getHost();
        this.port = uri.getPort();
    }


    @Override
    public void setService(ClientService clientService) {
        this.clientService = clientService;
    }

}
