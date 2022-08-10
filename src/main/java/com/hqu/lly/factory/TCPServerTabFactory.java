package com.hqu.lly.factory;

import com.hqu.lly.view.controller.ClientController;
import com.hqu.lly.view.controller.TCPServerController;
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
public class TCPServerTabFactory implements TabFactory{

    private String tabName = "server";

    private String tabPanePath = "tcpServerPane.fxml";
    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));

            Parent contentPane = loader.load();

            Object controller = loader.getController();

            tab.setContent(contentPane);

            if (controller instanceof TCPServerController) {
                tab.setOnClosed(event -> ((TCPServerController) controller).destroy());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
