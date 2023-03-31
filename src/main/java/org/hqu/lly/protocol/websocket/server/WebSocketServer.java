package org.hqu.lly.protocol.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.bean.ConnectedServer;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.protocol.websocket.server.initalizer.WebSocketServerChannelInitializer;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.service.impl.ServerService;

import java.net.BindException;

/**
 * <p>
 * websocket服务类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/7/2 10:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class WebSocketServer extends ConnectedServer {

    private int port;
    private String host;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private WebSocketServerChannelInitializer wsChannelInitializer = new WebSocketServerChannelInitializer();
    private Channel channel;
    private ConnectedServerService serverService;

    @Override
    public void init() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(wsChannelInitializer);

            channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            });
            log.info("websocket start successful at {}", channel.localAddress());
            serverService.onStart();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            if (e instanceof BindException) {
                serverService.onError(e, "该端口已被占用");
                log.warn(e.toString());
            } else {
                log.error("websocket start fail, cause: {}", e.toString());
            }
        }
    }

    @Override
    public Channel call() throws Exception {
        init();
        return channel;
    }


    @Override
    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.close();
        log.info("webSocket server closed");
    }

    @Override
    public void setService(ServerService serverService) {
        this.serverService = (ConnectedServerService) serverService;
        this.wsChannelInitializer.setServerService(this.serverService);
    }

    @Override
    public void sendMessage(String msg, Channel channel) {
        channel.writeAndFlush(new TextWebSocketFrame(msg));
        serverService.updateMsgList(new MsgLabel(MsgLabel.Type.SEND, channel.remoteAddress().toString(), msg));
    }

}
