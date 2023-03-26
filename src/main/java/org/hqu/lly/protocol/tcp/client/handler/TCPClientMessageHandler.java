package org.hqu.lly.protocol.tcp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * TCP客户端消息处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/4 19:55
 */
@Slf4j
public class TCPClientMessageHandler extends SimpleChannelInboundHandler<String> {  // 6

    private ClientService clientService;

    public TCPClientMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String serverAddr = ctx.channel().remoteAddress().toString();
        clientService.updateMsgList(new MsgLabel(MsgLabel.Type.RECEIVE, serverAddr, msg));
    }

}
