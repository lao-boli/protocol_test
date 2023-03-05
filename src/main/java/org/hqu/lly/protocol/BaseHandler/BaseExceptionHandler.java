package org.hqu.lly.protocol.BaseHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 统一异常处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/16 20:11
 */
@Slf4j
public class BaseExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        log.error(cause.toString());
        cause.printStackTrace();
        ctx.channel().close();
    }
}
