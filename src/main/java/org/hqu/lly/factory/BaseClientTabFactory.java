package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.ClientSessionConfig;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.view.controller.BaseClientController;

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
public class BaseClientTabFactory<T extends BaseClientController<?>> extends BaseTabFactory<T> {

    public BaseClientTabFactory() {
    }

    /**
     * <p>
     * 加载客户端面板.<br>
     * 不通过本地配置文件加载客户端面板时传入null调用.
     * </p>
     *
     * @param config 应为客户端面板配置类{@link ClientSessionConfig}.
     * @return {@link Tab} 客户端标签页.
     *  @date 2023-07-04 20:10
     */
    @Override
    public Tab create(Config config) {

        TitleTab tab = new TitleTab(tabName,tabPane);

        try {
            FXMLLoader loader = new FXMLLoader(tabPanePath);
            Parent contentPane = loader.load();

            T controller = loader.getController();
            controller.setTabTitle(tab.getTabTitleField());
            controller.init((ClientSessionConfig) config);

            tab.setContent(contentPane);
            tab.setOnClosed((controller)::destroy);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }

}
