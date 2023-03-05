package org.hqu.lly.protocol.mqtt.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.bean.ConnectedServer;
import org.hqu.lly.protocol.mqtt.server.handler.MQTTServerConnectHandler;
import org.hqu.lly.protocol.mqtt.server.handler.MQTTServerExceptionHandler;
import org.hqu.lly.protocol.mqtt.server.handler.MQTTServerMessageHandler;
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
public class MQTTServer extends ConnectedServer {

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

            serverBootstrap.option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_RCVBUF, 10485760);

            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(600, 600, 1200));
                    ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
                    ch.pipeline().addLast("decoder", new MqttDecoder());
                    ch.pipeline().addLast("MQTTServerConnectHandler", new MQTTServerConnectHandler(serverService));
                    ch.pipeline().addLast("MQTTServerMessageHandler", new MQTTServerMessageHandler(serverService));
                    ch.pipeline().addLast("MQTTExceptionHandler", new MQTTServerExceptionHandler());
                }
            });

            channel = serverBootstrap.bind(port).sync().channel();

            channel.closeFuture().addListener(promise -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                log.info("mqtt server closed");
            });
            log.info("mqtt server start successful at " + channel.localAddress());
            serverService.onStart();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            if (e instanceof BindException) {
                log.warn(e.toString());
                serverService.onError(e, "该端口已被占用");
            } else {
                log.error("mqtt server error", e);
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
        serverService.updateMsgList(formatSendMsg);
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

    public static void main(String[] args) {
        MQTTServer mqttServer = new MQTTServer();
        mqttServer.setPort(8888);
        mqttServer.setService(new ConnectedServerService() {
            @Override
            public void addChannel(Channel channel) {

            }

            @Override
            public void removeChannel(Channel channel) {

            }

            @Override
            public void updateMsgList(String msg) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onError(Throwable e, String errorMessage) {

            }

            @Override
            public void onClose() {

            }
        });
        mqttServer.init();
    }

}
