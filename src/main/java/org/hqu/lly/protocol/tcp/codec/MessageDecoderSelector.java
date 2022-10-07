package org.hqu.lly.protocol.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.utils.ChannelUtil;

/**
 * <p>
 * 根据channel类型选择解码器
 * <p>
 *
 * @author hqully
 * @date 2022/9/10 17:09
 * @version 1.0
 */
@Slf4j
public class MessageDecoderSelector extends ChannelInboundHandlerAdapter {

    public MessageDecoderSelector() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            if(ChannelUtil.isAppChannel(ctx)){
                ctx.fireChannelRead(msg);
            } else {
                // 跳过LTC解码器
                ChannelHandlerContext context = ctx.pipeline().context("LTCDecoder");
                context.fireChannelRead(msg);

            }
        }
    }

}
