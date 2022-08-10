package com.hqu.lly.factory;

import com.hqu.lly.view.controller.ClientController;
import javafx.event.Event;
import javafx.event.EventHandler;
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
public class TCPClientTabFactory implements TabFactory{

    private String tabName = "client";

    private String tabPanePath = "clientPane.fxml";
    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));

            Parent contentPane = loader.load();

            Object controller = loader.getController();

            tab.setContent(contentPane);

            if (controller instanceof ClientController) {
                tab.setOnClosed(event -> ((ClientController) controller).destroy());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
