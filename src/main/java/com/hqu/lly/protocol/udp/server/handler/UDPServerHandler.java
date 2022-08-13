package com.hqu.lly.protocol.udp.server.handler;

import com.hqu.lly.service.impl.ServerService;
import com.hqu.lly.utils.MsgFormatUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/12 20:08
 * @Version 1.0
 */
@Slf4j
public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    ConcurrentHashMap<SocketAddress,UDPChannel> udpClientGroup = new ConcurrentHashMap<>();

    private ServerService serverService;

    public UDPServerHandler(ServerService serverService) {
        this.serverService = serverService;

    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        String receiveText = datagramPacket.content().toString(CharsetUtil.UTF_8);

        String clientAddr = datagramPacket.sender().toString();

        String formatReceiveMsg = MsgFormatUtil.formatReceiveMsg(receiveText, clientAddr);

        serverService.updateMsgList(formatReceiveMsg);

        String responseText = "your message is " + receiveText;

        String formatSendMsg = MsgFormatUtil.formatSendMsg(responseText, clientAddr);

        ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseText, CharsetUtil.UTF_8), datagramPacket.sender()));

        serverService.updateMsgList(formatSendMsg);

        if (udpClientGroup.get(datagramPacket.sender()) == null){

            UDPChannel udpChannel = new UDPChannel(datagramPacket.sender());

            serverService.addChannel(udpChannel);

            addChannel(udpChannel);

        }

    }

    private void addChannel(UDPChannel channel) {

        udpClientGroup.put(channel.remoteAddress(),channel);

    }


    public class UDPChannel implements Channel {

        public UDPChannel(InetSocketAddress remoteAddress) {
            this.remoteAddress = remoteAddress;
        }

        @Setter
        private InetSocketAddress remoteAddress;

        @Override
        public SocketAddress remoteAddress(){
           return remoteAddress;
        }

        @Override
        public ChannelFuture closeFuture() {
            return null;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return 0;
        }

        @Override
        public long bytesBeforeWritable() {
            return 0;
        }

        @Override
        public Unsafe unsafe() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public Channel read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object o) {
            return null;
        }

        @Override
        public ChannelFuture write(Object o, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public Channel flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable throwable) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        @Override
        public ChannelId id() {
            return null;
        }

        @Override
        public EventLoop eventLoop() {
            return null;
        }

        @Override
        public Channel parent() {
            return null;
        }

        @Override
        public ChannelConfig config() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isRegistered() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public ChannelMetadata metadata() {
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress socketAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {

            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
            return false;
        }

        @Override
        public int compareTo(Channel o) {
            return 0;
        }
    }
}
