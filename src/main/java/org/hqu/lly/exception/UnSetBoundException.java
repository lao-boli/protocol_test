package org.hqu.lly.exception;

/**
 * <p>
 * 自定义数据未设置数据边界异常
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-01-28 10:21
 */
public class UnSetBoundException extends RuntimeException{

    public UnSetBoundException() {
        super("unset data bounds");

    }

    public UnSetBoundException(String message) {
        super(message);
    }

    public UnSetBoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSetBoundException(Throwable cause) {
        super(cause);
    }

    protected UnSetBoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
