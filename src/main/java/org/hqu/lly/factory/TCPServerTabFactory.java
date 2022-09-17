package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.view.controller.TCPServerController;

import java.io.IOException;

/**
 * <p>
 * TCP服务端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class TCPServerTabFactory implements TabFactory {

    private String tabName = "server";

    private String tabPanePath = ResLocConsts.TCP_SERVER_PANE;

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();
            TCPServerController controller = loader.getController();
            tab.setContent(contentPane);
            tab.setOnClosed(event -> controller.destroy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
