package org.hqu.lly.factory;

import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.protocol.udp.client.UDPClient;
import org.hqu.lly.view.controller.ClientController;
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
public class UDPClientTabFactory implements TabFactory{

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
                ((ClientController) controller).setProtocol(ProtocolConsts.UDP);
                ((ClientController) controller).setClient(new UDPClient());
                tab.setOnClosed(event -> ((ClientController) controller).destroy());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
