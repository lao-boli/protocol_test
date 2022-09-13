package org.hqu.lly.protocol.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.tcp.constant.CommonConsts;
import org.hqu.lly.protocol.tcp.constant.HeaderConsts;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;
import org.hqu.lly.protocol.tcp.server.group.TCPChannelGroup;

import java.util.Arrays;

/**
 * <p>
 * 根据消息类型选择编码器
 * <p>
 *
 * @author liulingyu
 * @version 1.0
 * @date 2022/9/10 17:09
 */
@Slf4j
public class MessageEncoderSelector extends ChannelOutboundHandlerAdapter {

    /**
     * 选择器类型
     */
    private Integer type;

    public MessageEncoderSelector(Integer type) {
        this.type = type;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        boolean isLocalApp = isApp(ctx);
        if (isLocalApp) {
            ctx.write(msg, promise);
        } else {
            // 跳过LTC编码器
            ChannelHandlerContext context = ctx.pipeline().context("LTCEncoder");
            context.write(msg,promise);
        }
    }

    private boolean isApp(ChannelHandlerContext ctx) {
        if (type.equals(CommonConsts.SERVER)){
            return AppChannelGroup.clientChannelSet.contains(ctx.channel().remoteAddress().toString());
        }else {
            return AppChannelGroup.serverChannelSet.contains(ctx.channel().remoteAddress().toString());
        }
    }

}
