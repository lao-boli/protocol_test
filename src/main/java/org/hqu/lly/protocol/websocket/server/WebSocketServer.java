package org.hqu.lly.protocol.websocket.server;

import org.hqu.lly.domain.base.BaseServer;
import org.hqu.lly.protocol.websocket.server.initalizer.WSChannelInitializer;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.MsgFormatUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;

/**
 * <p>
 * websocket服务类
 * <p>
 *
 * @author liulingyu
 * @date 2022/7/2 10:59
 * @version 1.0
 */
@Data
@Slf4j
public class WebSocketServer extends BaseServer {

    private String port;
    private String host;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private WSChannelInitializer wsChannelInitializer = new WSChannelInitializer();
    private Channel channel;

    private ServerService serverService;

    @Override
    public void init(){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //指定服务端连接队列长度，也就是服务端处理线程全部繁忙，并且队列长度已达到1024个，后续请求将会拒绝
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(wsChannelInitializer);

            ChannelFuture f = serverBootstrap.bind(Integer.parseInt(port)).sync();
            channel = f.channel();
            channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
            log.info("Netty websocket start successful at " + channel.localAddress());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.error("WS服务启动失败：" + e);

            if (e instanceof BindException){

                serverService.onError(e,"该端口已被占用");

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
        if (null == channel){
            return;
        }
        channel.close();
        log.info("webSocket server closed");
    }

    @Override
    public void setService(ServerService serverService) {
       this.serverService =  serverService;
       this.wsChannelInitializer.setServerService(this.serverService);
    }

    @Override
    public void sendMessage(String msg, Channel channel) {

        channel.writeAndFlush(new TextWebSocketFrame(msg));

        String formatSendMsg = MsgFormatUtil.formatSendMsg(msg, channel.remoteAddress().toString());

        serverService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);
    }
}
