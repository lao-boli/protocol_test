package org.hqu.lly.protocol.mqtt.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.protocol.mqtt.server.group.MQTTChannelGroup;
import org.hqu.lly.protocol.mqtt.server.util.MqttMsgBack;
import org.hqu.lly.service.impl.ConnectedServerService;

import java.util.List;

import static org.hqu.lly.protocol.mqtt.server.group.MQTTChannelGroup.topicTrees;

/**
 * <p>
 * 管理TCPChannel的连接
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/16 20:04
 */
@Slf4j
@ChannelHandler.Sharable
public class MQTTServerConnectHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ConnectedServerService serverService;

    @Setter
    private Channel bindChannel;

    public MQTTServerConnectHandler(ConnectedServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("server: {} connect with client: {}", channel.localAddress(), channel.remoteAddress());
        serverService.updateMsgList(new MsgLabel("连接成功,客户端地址为: " + channel.remoteAddress().toString()));
        serverService.addChannel(channel);
        MQTTChannelGroup.clientChannelGroup.put(channel.id().toString(), channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        if (null != msg) {
            log.info("info--" + msg.toString());
            MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
            Channel channel = ctx.channel();
            if (mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)) {
                //	在一个网络连接上，客户端只能发送一次CONNECT报文。服务端必须将客户端发送的第二个CONNECT报文当作协议违规处理并断开客户端的连接
                //	to do 建议connect消息单独处理，用来对客户端进行认证管理等 这里直接返回一个CONNACK消息
                MqttMsgBack.connack(channel, msg);
            }

            switch (mqttFixedHeader.messageType()) {
                //	客户端发布消息
                case PUBLISH:
                    //	PUBACK报文是对QoS 1等级的PUBLISH报文的响应
                    // MqttMsgBack.puback(channel, msg);
                    // 传递给下一个handler进行消息的分发
                    ctx.fireChannelRead(((MqttPublishMessage) msg).retain());

                    break;
                //	发布释放
                case PUBREL:
                    //	PUBREL报文是对PUBREC报文的响应
                    //	to do
                    MqttMsgBack.pubcomp(channel, msg);
                    break;
                //	客户端订阅主题
                case SUBSCRIBE:
                    //	客户端向服务端发送SUBSCRIBE报文用于创建一个或多个订阅，每个订阅注册客户端关心的一个或多个主题。
                    //	为了将应用消息转发给与那些订阅匹配的主题，服务端发送PUBLISH报文给客户端。
                    //	SUBSCRIBE报文也（为每个订阅）指定了最大的QoS等级，服务端根据这个发送应用消息给客户端
                    // 	to do
                    MqttMsgBack.suback(channel, msg);
                    List<MqttTopicSubscription> subscriptions = ((MqttSubscribePayload) msg.payload()).topicSubscriptions();
                    subscriptions.forEach(item -> topicTrees.get(bindChannel).addRoute(item.topicName(),channel));
                    break;
                //	客户端取消订阅
                case UNSUBSCRIBE:
                    //	客户端发送UNSUBSCRIBE报文给服务端，用于取消订阅主题
                    //	to do
                    MqttMsgBack.unsuback(channel, msg);
                    topicTrees.get(bindChannel).removeRoute(((MqttUnsubscribeMessage) msg).payload().topics().get(0),channel);
                    break;
                //	客户端发起心跳
                case PINGREQ:
                    //	客户端发送PINGREQ报文给服务端的
                    //	在没有任何其它控制报文从客户端发给服务的时，告知服务端客户端还活着
                    //	请求服务端发送 响应确认它还活着，使用网络以确认网络连接没有断开
                    MqttMsgBack.pingresp(channel, msg);
                    break;
                //	客户端主动断开连接
                case DISCONNECT:
                    //	DISCONNECT报文是客户端发给服务端的最后一个控制报文， 服务端必须验证所有的保留位都被设置为0
                    //	to do
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        MQTTChannelGroup.clientChannelGroup.remove(channel.id().toString());
        log.info("server: {} disconnect form client: {}", channel.localAddress(), channel.remoteAddress());
        serverService.updateMsgList(new MsgLabel("与客户端断开连接, 客户端地址: " + channel.remoteAddress().toString()));
        serverService.removeChannel(channel);
    }



}
