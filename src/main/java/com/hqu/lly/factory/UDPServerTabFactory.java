package com.hqu.lly.factory;

import com.hqu.lly.protocol.tcp.server.TCPServer;
import com.hqu.lly.protocol.udp.server.UDPServer;
import com.hqu.lly.view.controller.ServerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

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
public class UDPServerTabFactory implements TabFactory{

    private String tabName = "server";

    private String tabPanePath = "serverPane.fxml";
    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));

            Parent contentPane = loader.load();

            Object controller = loader.getController();

            tab.setContent(contentPane);

            if (controller instanceof ServerController) {
                ((ServerController) controller).setServer(new UDPServer());
                tab.setOnClosed(event -> ((ServerController) controller).destroy());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
