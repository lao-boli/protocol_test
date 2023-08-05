package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import org.hqu.lly.domain.component.MyAlert;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.domain.config.ServerSessionConfig;
import org.hqu.lly.view.controller.BaseServerController;

import java.io.IOException;
import java.util.Optional;

/**
 * <p>
 * 基础客户端标签页工厂
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-01-29 20:25
 */
public class BaseServerTabFactory<T extends BaseServerController<?>> extends BaseTabFactory<T> {


    public BaseServerTabFactory() {
    }

    /**
     * <p>
     * 加载服务面板tab.<br>
     * 不通过本地配置文件加载服务端面板时传入null调用.
     * </p>
     *
     * @param config 应为服务面板配置类{@link ServerSessionConfig}.
     * @return {@link Tab} 服务端标签页.
     * @date 2023-07-04 20:27
     */
    @Override
    public Tab create(Config config) {
        TitleTab tab = new TitleTab(tabName, tabPane);
        try {
            FXMLLoader loader = new FXMLLoader(tabPanePath);
            Parent contentPane = loader.load();

            T controller = loader.getController();
            controller.setTab(tab);
            controller.init((ServerSessionConfig) config);

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
