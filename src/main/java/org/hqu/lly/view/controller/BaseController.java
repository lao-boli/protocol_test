package org.hqu.lly.view.controller;

import org.hqu.lly.domain.config.Config;

/**
 * <p>
 * 服务端或客户端controller顶层父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/2 19:42
 */
public abstract class BaseController {

    /**
     * <p>
     *     保存服务端或客户端controller配置
     * </p>
     * @return {@link Config} 服务端或客户端面板配置
     * @date 2023-02-02 19:47:08 <br>
     * @author hqully <br>
     */
    public abstract Config saveAndGetConfig();

}
