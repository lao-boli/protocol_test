package org.hqu.lly.protocol.websocket.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.BaseHandler.BaseExceptionHandler;
import org.hqu.lly.service.impl.ClientService;

/**
 * <p>
 * WebSocket客户端统一异常处理
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/20 19:31
 */
@Slf4j
public class WebSocketClientExceptionHandler extends BaseExceptionHandler {

    private ClientService clientService;

    public WebSocketClientExceptionHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof WebSocketClientHandshakeException) {
            log.warn("handshake fail: {}", cause.toString());
            HttpResponse response = ((WebSocketClientHandshakeException) cause).response();
            if (response.status().equals(HttpResponseStatus.NOT_FOUND)) {
                clientService.onError(cause, "请求的WebSocket路径错误");
            } else {
                clientService.onError(cause, "与服务器握手失败");
            }
        } else {
            log.error("occur exception: {}", cause.toString());
        }
        super.exceptionCaught(ctx, cause);
    }

}
