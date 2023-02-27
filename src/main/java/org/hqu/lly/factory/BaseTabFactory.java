package org.hqu.lly.factory;

import javafx.scene.control.TabPane;
import lombok.Getter;
import lombok.Setter;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.BaseClientController;
import org.hqu.lly.view.controller.BaseController;
import org.hqu.lly.view.controller.BaseServerController;

import java.net.URL;

/**
 * <p>
 * 基础标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-02-25 21:48
 */
public abstract class BaseTabFactory<T extends BaseController> implements TabFactory<T> {

    /**
     * 标签页名称
     */
    protected String tabName;

    /**
     * 标签页所在路径URL,<br>
     * 应为{@link ResLoc}中的值.
     */
    protected URL tabPanePath;

    /**
     * 标签页面板对应控制器<br>
     * 应为{@link BaseClientController}
     * 或 {@link BaseServerController}的子类.
     */
    @Getter
    protected T controller;

    /**
     * 标签页所在的标签面板
     */
    @Setter
    protected TabPane tabPane;

    public BaseTabFactory() {
    }


}
