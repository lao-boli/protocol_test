package org.hqu.lly.view.controller;

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


    protected boolean destroyed = false;

    public abstract void save();

    /**
     * <p>
     * 判断客户端或服务端的控制器是否已销毁
     * </p>
     * @return 若已销毁返回true,否则返回false
     * @date 2023-02-08 11:30:14 <br>
     */
    public boolean isDestroyed(){
        return destroyed;
    }

}
