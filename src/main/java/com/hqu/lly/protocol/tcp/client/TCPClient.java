package com.hqu.lly.protocol.tcp.client;

import com.hqu.lly.common.BaseClient;
import com.hqu.lly.protocol.tcp.client.handler.TCPClientMessageHandler;
import com.hqu.lly.service.impl.ClientService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
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
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new TCPClientMessageHandler(clientService));

                        }
                    });
            //绑定服务器
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            this.channel.closeFuture().addListener((ChannelFutureListener) future -> {
                log.debug("处理关闭之后的操作");
                eventLoopGroup.shutdownGracefully();
            });
        } catch (Exception e) {

            log.error("tcp client error", e);

            eventLoopGroup.shutdownGracefully();

            if (e instanceof ConnectException) {

                clientService.onError(e, "该地址服务未开启");

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
