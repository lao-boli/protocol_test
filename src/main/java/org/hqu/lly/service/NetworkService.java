package org.hqu.lly.service;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @version 1.0
 * @date 2022/8/13 20:26
 */
public interface NetworkService {

    /**
     * <p>
     * 网络连接发生异常时的回调
     * </p>
     *
     * @param e            发生的异常
     * @param errorMessage 自定义的异常信息
     * @return void
     * @date 2022-09-05 14:47:09 <br>
     * @author liulingyu <br>
     */
    void onError(Throwable e, String errorMessage);

    /**
     * <p>
     * 网络连接关闭时的回调
     * </p>
     *
     * @param
     * @return void
     * @date 2022-09-05 14:48:26 <br>
     * @author liulingyu <br>
     */
    void onClose();
}
