package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.MyAlert;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.domain.config.TCPServerSessionConfig;
import org.hqu.lly.view.controller.TCPServerController;

import java.io.IOException;
import java.util.Optional;

/**
 * <p>
 * TCP服务端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:57
 */
public class TCPServerTabFactory extends BaseServerTabFactory<TCPServerController>{

    public TCPServerTabFactory(){
        super.tabName = "server";
        super.tabPanePath = ResLoc.TCP_SERVER_PANE;
    }

    @Override
    public Tab create(Config config) {
        TitleTab tab = new TitleTab(tabName, tabPane);
        try {
            FXMLLoader loader = new FXMLLoader(tabPanePath);
            Parent contentPane = loader.load();

            TCPServerController controller = loader.getController();
            controller.setTab(tab);
            controller.init((TCPServerSessionConfig) config);

            tab.setContent(contentPane);
            tab.setOnCloseRequest(() -> {
                Optional<ButtonType> result = new MyAlert(Alert.AlertType.CONFIRMATION, "提示", "关闭标签页后将丢失本标签页的数据以及配置文件,是否继续?").showAndWait();
                return result.get().equals(ButtonType.OK);
            });
            tab.setOnClosed((controller)::destroy);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }

}
