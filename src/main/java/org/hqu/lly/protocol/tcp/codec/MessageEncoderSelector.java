package org.hqu.lly.protocol.tcp.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.utils.ChannelUtil;

/**
 * <p>
 * 根据channel类型选择编码器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/10 17:09
 */
@Slf4j
public class MessageEncoderSelector extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (ChannelUtil.isAppChannel(ctx)) {
            ctx.write(msg, promise);
        } else {
            // 跳过LTC编码器
            ChannelHandlerContext context = ctx.pipeline().context("LTCEncoder");
            context.write(msg,promise);
        }
    }

}
