package org.hqu.lly.protocol.tcp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.hqu.lly.utils.MsgUtil;

/**
 * <p>
 * TCP服务端消息处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/4 19:55
 */
@Slf4j
public class TCPServerMessageHandler extends SimpleChannelInboundHandler<String> {

    private ConnectedServerService serverService;

    public TCPServerMessageHandler(ConnectedServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String clientAddr = ctx.channel().remoteAddress().toString();
        String receiveText = msg;
        String formatReceiveMsg = MsgUtil.formatReceiveMsg(receiveText, clientAddr);
        serverService.updateMsgList(formatReceiveMsg);
        log.info(formatReceiveMsg);

        String responseText = "your message is " + msg;
        String formatSendMsg = MsgUtil.formatSendMsg(responseText, clientAddr);
        serverService.updateMsgList(formatSendMsg);
        log.info(formatSendMsg);

        ctx.channel().writeAndFlush(responseText);
    }

}
