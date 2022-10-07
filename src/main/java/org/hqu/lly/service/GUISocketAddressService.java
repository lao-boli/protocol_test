package org.hqu.lly.service;

import java.net.InetSocketAddress;

/**
 * <p>
 * GUI界面socket地址服务
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/17 19:58
 */
public interface GUISocketAddressService {
    /**
     * <p>
     *     添加目标端口地址
     * </p>
     * @param dstAddr 目标端口地址
     * @return  void
     * @date 2022-09-17 20:01:46 <br>
     * @author hqully <br>
     */
    void addInetSocketAddress(InetSocketAddress dstAddr);

    /**
     * <p>
     *     移除目标端口地址
     * </p>
     * @param dstAddr 目标端口地址
     * @return  void
     * @date 2022-09-17 20:01:46 <br>
     * @author hqully <br>
     */
    void removeInetSocketAddress(InetSocketAddress dstAddr);
}
