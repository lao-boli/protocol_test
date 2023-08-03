package org.hqu.lly.view.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.domain.config.SessionConfig;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.enums.TabFactoryEnum;
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
    private String tabPaneName;

    public TabPaneController() {
    }

    public void setTabFactory(PaneType paneType) {
        this.tabFactory = TabFactoryEnum.getByPaneType(paneType).getTabFactory();
        this.tabFactory.setTabPane(mainTabPane);
    }


    @FXML
    void createNewTab(Event event) {
        // 页面初始化时会触发一次tab selection change 事件，
        // 此时本方法会触发,从配置文件加载时，生成一个无配置的new tab会导致异常，
        // 原因为生成新tab会创建一个config并加入sessionconfig中，而在迭代中不能新增或删除元素。
        // 所以这里需要忽略掉初始化时触发的tab selection change 事件
        // tabFactory 也应在controller初始化完成后进行设置.
        if (tabFactory != null) {
            createNewTab();
        }
    }

    /**
     * <p>
     * 创建一个默认的新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab() {
        createNewTab((SessionConfig)null);
    }

    /**
     * <p>
     * 以配置类创建一个新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab(SessionConfig config) {
        Tab tab = tabFactory.create(config);

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);
        if (config != null){
            if (config.getTabSelected()){
                mainTabPane.getSelectionModel().select(tab);
            }
            return;
        }
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
