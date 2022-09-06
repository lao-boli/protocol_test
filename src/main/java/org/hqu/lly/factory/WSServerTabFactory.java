package org.hqu.lly.factory;

import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.protocol.websocket.server.WebSocketServer;
import org.hqu.lly.view.controller.UDPServerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import org.hqu.lly.view.controller.WebSocketServerController;

import java.io.IOException;

/**
 * <p>
 * tcp client tab factory
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/7 14:57
 * @Version 1.0
 */
public class WSServerTabFactory implements TabFactory {

    private String tabName = "server";

    private String tabPanePath = ResLocConsts.WEB_SOCKET_SERVER_PANE;

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();
            WebSocketServerController controller = loader.getController();

            tab.setContent(contentPane);
            tab.setOnClosed(event -> controller.destroy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
