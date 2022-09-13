package org.hqu.lly.protocol.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.tcp.constant.HeaderConsts;

import java.util.Arrays;

/**
 * <p>
 * 根据消息类型选择解码器
 * <p>
 *
 * @author liulingyu
 * @date 2022/9/10 17:09
 * @Version 1.0
 */
@Slf4j
public class MessageDecoderSelector extends ChannelInboundHandlerAdapter {

    public MessageDecoderSelector() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            byte[] inMagicNum = ByteBufUtil.getBytes((ByteBuf) msg, 0, 4, true);
            boolean isLTC = Arrays.equals(HeaderConsts.MAGIC_NUMBER, inMagicNum);
            if (isLTC) {
                ctx.fireChannelRead(msg);
            } else {
                // 跳过LTC解码器
                ChannelHandlerContext context = ctx.pipeline().context("LTCDecoder");
                context.fireChannelRead(msg);
            }
        }
    }

}
