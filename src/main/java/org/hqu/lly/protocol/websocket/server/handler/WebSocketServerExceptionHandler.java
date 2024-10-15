package org.hqu.lly.protocol.websocket.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.base.handler.BaseExceptionHandler;

/**
 * <p>
 * WebSocket服务端统一异常处理
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/20 19:31
 */
@Slf4j
public class WebSocketServerExceptionHandler extends BaseExceptionHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*
         * TODO maybe handle special exception someday
         */
        super.exceptionCaught(ctx, cause);
    }

}
