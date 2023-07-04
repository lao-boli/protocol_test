package org.hqu.lly.view.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.domain.config.SessionConfig;
import org.hqu.lly.domain.config.TabPaneConfig;
import org.hqu.lly.factory.BaseTabFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * 标签面板控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 9:51
 */
@Slf4j
public class TabPaneController extends BaseController implements Initializable {

    /**
     * fxml的tabPane节点
     */
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab createTab;
    private BaseTabFactory tabFactory;
    /**
     * 标签页面板名称<br>
     * 应为 {@link ContentPaneConsts}中的一种.
     */
    @Setter
    private String tabPaneName;
    /**
     * 本页面配置类
     */
    @Setter
    private TabPaneConfig tabPaneConfig;

    public TabPaneController() {
    }

    public void setTabFactory(BaseTabFactory tabFactory) {
        this.tabFactory = tabFactory;
    }


    @FXML
    void createNewTab(Event event) {
        if (tabFactory != null) {
            createNewTab();
        }
    }

    // XXX 待优化两个创建新标签页的方法实现

    /**
     * <p>
     * 创建一个默认的新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab() {
        tabFactory.setTabPane(mainTabPane);
        Tab tab = tabFactory.create(null);

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }

    /**
     * <p>
     * 以配置类创建一个新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab(SessionConfig config) {
        tabFactory.setTabPane(mainTabPane);
        Tab tab = tabFactory.create(config);

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 阻止"+"标签页的拖拽事件
        createTab.getGraphic().addEventHandler(MouseEvent.MOUSE_DRAGGED, Event::consume);
        ObservableList<Tab> tabs = mainTabPane.getTabs();
        tabs.addListener((ListChangeListener<Tab>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    // 若最后一个标签页不是"+",则替换为"+"
                    if (!tabs.get(tabs.size() - 1).equals(createTab)) {
                        tabs.remove(createTab);
                        tabs.add(createTab);
                    }
                }
            }
        });
    }


    @Override
    public void save() {

    }

}