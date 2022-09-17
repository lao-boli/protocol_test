package org.hqu.lly.protocol.tcp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.BaseHandler.BaseExceptionHandler;

/**
 * <p>
 * TCP服务端异常统一处理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/16 20:10
 */
@Slf4j
public class TCPServerExceptionHandler extends BaseExceptionHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
