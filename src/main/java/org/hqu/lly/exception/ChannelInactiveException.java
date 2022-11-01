package org.hqu.lly.exception;

/**
 * <p>
 * nettyChannel不活动异常
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/11/1 9:07
 */
public class ChannelInactiveException extends RuntimeException{

    public ChannelInactiveException() {
        super("netty channel inactive");

    }

    public ChannelInactiveException(String message) {
        super(message);
    }

    public ChannelInactiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelInactiveException(Throwable cause) {
        super(cause);
    }

    protected ChannelInactiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
