package org.hqu.lly.factory;

import org.hqu.lly.constant.ProtocolConsts;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.protocol.udp.client.UDPClient;
import org.hqu.lly.view.controller.UDPClientController;
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
public class UDPClientTabFactory implements TabFactory {

    private String tabName = "client";

    private String tabPanePath = ResLocConsts.UDP_CLIENT_PANE;

    @Override
    public Tab create() {
        Tab tab = new Tab(tabName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabPanePath));
            Parent contentPane = loader.load();

            UDPClientController controller = loader.getController();
            tab.setContent(contentPane);

            tab.setOnClosed(event -> controller.destroy());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
}
