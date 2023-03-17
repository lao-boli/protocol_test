package org.hqu.lly.protocol.mqtt.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.mqtt.router.TopicNode;
import org.hqu.lly.service.impl.ConnectedServerService;

import java.util.HashSet;
import java.util.Set;

import static org.hqu.lly.protocol.mqtt.server.group.MQTTChannelGroup.topicTrees;

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
@ChannelHandler.Sharable
public class MQTTServerMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ConnectedServerService serverService;

    @Setter
    private Channel bindChannel;

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
        Set<Channel> channelSet = new HashSet<>();
        String topicName = recMsg.variableHeader().topicName();
        TopicNode root = topicTrees.get(bindChannel);
        root.getValues(topicName, channelSet);
        channelSet.forEach(channel -> channel.writeAndFlush(message.retain()));
    }

}
