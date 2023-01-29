package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
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
public class BaseServerTabFactory<T extends BaseServerController<?>> implements TabFactory {

    protected String tabName;

    protected String tabPanePath;

    public BaseServerTabFactory() {
    }

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();
            T controller = loader.getController();

            tab.setContent(contentPane);
            tab.setOnClosed(event -> (controller).destroy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
