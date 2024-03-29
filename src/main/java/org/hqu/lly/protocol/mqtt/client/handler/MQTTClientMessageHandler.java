package org.hqu.lly.protocol.mqtt.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.utils.MsgUtil;

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
public class MQTTClientMessageHandler extends SimpleChannelInboundHandler<String> {  // 6

    private ClientService clientService;

    public MQTTClientMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String serverAddr = ctx.channel().remoteAddress().toString();
        String receiveText = msg;
        String formattedReceiveText = MsgUtil.formatReceiveMsg(receiveText, serverAddr);
        clientService.updateMsgList(new MsgLabel(formattedReceiveText));
        log.info(formattedReceiveText);
    }

}
