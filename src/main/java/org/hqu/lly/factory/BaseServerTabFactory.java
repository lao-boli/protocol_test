package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.domain.config.ServerConfig;
import org.hqu.lly.domain.config.ServerSessionConfig;
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
public class BaseServerTabFactory<T extends BaseServerController<?>> extends BaseTabFactory<T>{


    public BaseServerTabFactory() {
    }

    @Override
    public Tab create() {
        TitleTab tab = new TitleTab(tabName, tabPane);
        try {
            FXMLLoader loader = new FXMLLoader(tabPanePath);
            Parent contentPane = loader.load();
            // 不能直接赋值给成员变量，
            // 否则会导致标签页的关闭回调里的controller
            // 永远只有最新创建的tab页的controller
            T controller = loader.getController();
            controller.setTabTitle(tab.getTabTitleField());

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
    public Tab create(Config config) {
        TitleTab tab = new TitleTab(tabName, tabPane);
        try {
            FXMLLoader loader = new FXMLLoader(tabPanePath);
            Parent contentPane = loader.load();
            // 不能直接赋值给成员变量，
            // 否则会导致标签页的关闭回调里的controller
            // 永远只有最新创建的tab页的controller
            T controller = loader.getController();
            controller.setTabTitle(tab.getTabTitleField());
            controller.init((ServerSessionConfig) config);

            tab.setContent(contentPane);
            tab.setOnClosed(event -> (controller).destroy());

            // 设置完回调以后再赋值给成员变量，方便调用者获取。
            this.controller = controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
