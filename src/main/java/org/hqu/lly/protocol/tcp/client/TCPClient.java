package org.hqu.lly.protocol.tcp.client;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.EqualsAndHashCode;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.protocol.tcp.client.handler.TCPClientMessageHandler;
import org.hqu.lly.protocol.tcp.codec.LTCEncoder;
import org.hqu.lly.protocol.tcp.codec.MessageDecoderSelector;
import org.hqu.lly.protocol.tcp.codec.MessageEncoderSelector;
import org.hqu.lly.protocol.tcp.constant.CommonConsts;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.net.URI;

/**
 * <p>
 * TCP client
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/4 10:46
 * @Version 1.0
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

        clientService.updateMsgList(MsgFormatUtil.formatSendMsg(message, channel.remoteAddress().toString()));
    }


    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }

    @Override
    public void init() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        this.eventLoopGroup = eventLoopGroup;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("MessageDecoderSelector",new MessageDecoderSelector());
                            ch.pipeline().addLast("LTCDecoder",new LengthFieldBasedFrameDecoder(1024*100,4,4,0,8));
                            ch.pipeline().addLast("StringDecoder",new StringDecoder());
                            ch.pipeline().addLast("StringEncoder",new StringEncoder());
                            ch.pipeline().addLast("LTCEncoder",new LTCEncoder());
                            ch.pipeline().addLast("MessageEncoderSelector",new MessageEncoderSelector(CommonConsts.CLIENT));
                            ch.pipeline().addLast("TCPClientMessageHandler",new TCPClientMessageHandler(clientService));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            this.channel.closeFuture().addListener((ChannelFutureListener) future -> {
                log.debug("处理关闭之后的操作");
                eventLoopGroup.shutdownGracefully();
            });
            AppChannelGroup.clientChannelSet.add(channel.localAddress().toString());
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();

            if (e instanceof ConnectException) {
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
