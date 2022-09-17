package org.hqu.lly.protocol.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.protocol.tcp.constant.HeaderConsts;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * LTC encoder
 * <p>
 *
 * @author hqully
 * @date 2022/9/12 19:08
 * @version 1.0
 */
@Slf4j
public class LTCEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        // 写入魔数
        out.writeBytes(HeaderConsts.MAGIC_NUMBER);
        // 字符串转byte数组
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        // 写入消息长度
        out.writeInt(msgBytes.length);
        // 写入消息
        out.writeBytes(msgBytes);
    }

}
