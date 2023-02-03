package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import lombok.Getter;
import org.hqu.lly.domain.config.ClientConfig;
import org.hqu.lly.view.controller.BaseClientController;
import org.hqu.lly.view.group.ControllerGroup;

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
public class BaseClientTabFactory<T extends BaseClientController<?>> implements TabFactory<T> {
    protected String tabName;

    protected String tabPanePath;

    @Getter
    protected T controller;

    public BaseClientTabFactory() {
    }

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();
            controller = loader.getController();
            ControllerGroup.clientControllerSet.add(controller);

            tab.setContent(contentPane);
            tab.setOnClosed(event -> (controller).destroy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }

    public Tab createByConfig(ClientConfig config) {
        Tab tab = create();
        controller.initByConfig(config);
        return tab;
    }
}
