package org.hqu.lly.protocol.mqtt.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.impl.ConnectedServerService;

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
public class MQTTServerMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ConnectedServerService serverService;

    public MQTTServerMessageHandler(ConnectedServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        log.info(msg.toString());

        MqttPublishMessage recMsg = (MqttPublishMessage) msg;
        MqttFixedHeader recFixHeader = recMsg.fixedHeader();

        MqttFixedHeader pubFixHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, recFixHeader.isDup(), recFixHeader.qosLevel(), recFixHeader.isRetain(), 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(recMsg.variableHeader().topicName(), 0);
        ByteBuf copyPayload = recMsg.payload().copy();
        MqttPublishMessage message = (MqttPublishMessage) MqttMessageFactory.newMessage(pubFixHeader, variableHeader, Unpooled.buffer().writeBytes(copyPayload));
        ctx.writeAndFlush(message);


/*
        String clientAddr = ctx.channel().remoteAddress().toString();
        String receiveText = copyPayload.toString(StandardCharsets.UTF_8);
        String formatReceiveMsg = MsgUtil.formatReceiveMsg(receiveText, clientAddr);
        serverService.updateMsgList(formatReceiveMsg);
        log.info(formatReceiveMsg);

        String responseText = "your message is " + msg;
        String formatSendMsg = MsgUtil.formatSendMsg(responseText, clientAddr);
        serverService.updateMsgList(formatSendMsg);
        log.info(formatSendMsg);
*/
    }

}
