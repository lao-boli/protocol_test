package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import lombok.Getter;
import org.hqu.lly.domain.config.ServerConfig;
import org.hqu.lly.domain.config.TabConfig;
import org.hqu.lly.view.controller.BaseServerController;

import java.io.IOException;

/**
 * <p>
 * 基础客户端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-01-29 20:25
 */
public class BaseServerTabFactory<T extends BaseServerController<?>> implements TabFactory<T> {

    /**
     * 标签页名称
     */
    protected String tabName;

    /**
     * 标签页所在路径,<br>
     * 应为{@link org.hqu.lly.constant.ResLocConsts}中的值.
     */
    protected String tabPanePath;

    /**
     * 标签页d面板对应控制器,<br>
     * 应为{@link BaseServerController}的子类.
     */
    @Getter
    private T controller;

    public BaseServerTabFactory() {
    }

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();
            // 不能直接赋值给成员变量，
            // 否则会导致标签页的关闭回调里的controller
            // 永远只有最新创建的tab页的controller
            T controller = loader.getController();

            tab.setContent(contentPane);
            tab.setOnClosed(event -> (controller).destroy());

            // 设置完回调以后再赋值给成员变量，方便调用者获取。
            this.controller = controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }

    /**
     * <p>
     *     通过本地配置文件加载服务面板.<br>
     *     本方法应只在通过本地配置文件加载服务面板时调用.
     * </p>
     * @param config 应为服务面板配置类{@link ServerConfig}.
     * @return {@link Tab} 服务端标签页.
     * @date 2023-02-05 17:32:00 <br>
     */
    @Override
    public Tab create(TabConfig config) {
        Tab tab = create();
        controller.initByConfig((ServerConfig) config);
        return tab;
    }
}
