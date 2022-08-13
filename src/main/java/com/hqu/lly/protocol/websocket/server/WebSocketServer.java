package com.hqu.lly.protocol.websocket.server;

import com.hqu.lly.common.BaseServer;
import com.hqu.lly.protocol.websocket.server.initalizer.WSChannelInitializer;
import com.hqu.lly.service.ChannelService;
import com.hqu.lly.service.UIService;
import com.hqu.lly.utils.MsgFormatUtil;
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

import java.util.concurrent.Callable;

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
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private WSChannelInitializer wsChannelInitializer = new WSChannelInitializer();
    private Channel channel;

    private ChannelService channelService;

    @Override
    public void init(){
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
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.error("WS服务启动失败：" + e);
        }

    }
    @Override
    public Channel call() throws Exception {
        init();
        log.info("Netty websocket start successful at " + channel.localAddress());

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
    public void setService(UIService uiService) {
       this.channelService = (ChannelService) uiService;
       this.wsChannelInitializer.setChannelService(channelService);
    }

    @Override
    public void sendMessage(String msg, Channel channel) {

        channel.writeAndFlush(new TextWebSocketFrame(msg));

        String formatSendMsg = MsgFormatUtil.formatSendMsg(msg, channel.remoteAddress().toString());

        channelService.updateMsgList(formatSendMsg);

        log.info(formatSendMsg);
    }
}
