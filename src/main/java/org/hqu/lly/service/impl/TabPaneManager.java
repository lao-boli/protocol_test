package org.hqu.lly.service.impl;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import lombok.SneakyThrows;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.enums.TabFactoryEnum;
import org.hqu.lly.service.SwitchPaneService;
import org.hqu.lly.view.controller.TabPaneController;
import org.hqu.lly.view.group.ContentPaneGroup;
import org.hqu.lly.view.group.ControllerGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 标签面板管理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 19:22
 */
public class TabPaneManager implements SwitchPaneService {
    private static final Map<String,String> tabResMap = new HashMap<String,String>();
    static {
        tabResMap.put(ContentPaneConsts.WEB_SOCKET_SERVER_PANE, ResLocConsts.WEB_SOCKET_SERVER_PANE);
        tabResMap.put(ContentPaneConsts.WEB_SOCKET_CLIENT_PANE, ResLocConsts.WEB_SOCKET_CLIENT_PANE);
        tabResMap.put(ContentPaneConsts.TCP_SERVER_PANE, ResLocConsts.TCP_SERVER_PANE);
        tabResMap.put(ContentPaneConsts.TCP_CLIENT_PANE, ResLocConsts.TCP_CLIENT_PANE);
        tabResMap.put(ContentPaneConsts.UDP_SERVER_PANE, ResLocConsts.UDP_SERVER_PANE);
        tabResMap.put(ContentPaneConsts.UDP_CLIENT_PANE, ResLocConsts.UDP_CLIENT_PANE);
    }

    /**
     * 父面板
     */
    private Pane rootPane;

    /**
     * 标签面板名称
     */
    private String tabPaneName;

    /**
     * 标签面板路径
     */
    private String tabPanePath = ResLocConsts.TAB_PANE;

    public TabPaneManager(Pane rootPane, String contentPaneName) {
        this.rootPane = rootPane;
        this.tabPaneName = contentPaneName;
    }

    @Override
    public void switchPane() {
        // 如果当前存在面板,则先移除(此处面板的size只会是0或1)
        if (rootPane.getChildren().size() > 0) {
            rootPane.getChildren().remove(0);
        }
        // 根据面板名获取要切换的面板实例，若不存在则创建
        Parent contentPane = ContentPaneGroup.contentPaneMap.get(tabPaneName);
        if (contentPane != null) {
            rootPane.getChildren().add(contentPane);
        } else {
            rootPane.getChildren().add(createContentPane(tabPanePath));
        }
    }

    @SneakyThrows
    private Parent createContentPane(String contentPanePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(contentPanePath));
        Parent contentPane = loader.load();
        Object controller = loader.getController();
        if (controller instanceof TabPaneController) {
            // 新建面板默认创建一个标签页
            ((TabPaneController) controller).setTabFactory(TabFactoryEnum.getByPaneType(tabPaneName).getTabFactory());
            ((TabPaneController) controller).createNewTab();
            ((TabPaneController) controller).setTabPaneName(tabPaneName);
            ControllerGroup.tabPaneControllerSet.add((TabPaneController) controller);
        }
        ContentPaneGroup.contentPaneMap.put(tabPaneName, contentPane);
        return contentPane;
    }

//    @SneakyThrows
//    public static Parent createContentPaneByConfig() {
//        TopConfig config = TopConfig.getInstance();
//        for (String paneName : config.getContentPaneNames()) {
//
//        }
//        FXMLLoader loader = new FXMLLoader(TabPaneManager.class.getClassLoader().getResource(tabResMap.get(config.getTabName())));
//        Parent contentPane = loader.load();
//        Object controller = loader.getController();
//        if (controller instanceof TabPaneController) {
//            // 新建面板默认创建一个标签页
//            ((TabPaneController) controller).setTabFactory(TabFactoryEnum.getByPaneType(config.getTabName()).getTabFactory());
//            ((TabPaneController) controller).createNewTab();
//        }
//        ContentPaneGroup.contentPaneMap.put(config.getTabName(), contentPane);
//        return contentPane;
//    }

//    @SneakyThrows
//    public static Parent createContentPaneByConfig(Config config) {
//        FXMLLoader loader = new FXMLLoader(TabPaneManager.class.getClassLoader().getResource(tabResMap.get(config.getTabName())));
//        Parent contentPane = loader.load();
//        Object controller = loader.getController();
//        if (controller instanceof TabPaneController) {
//            // 新建面板默认创建一个标签页
//            ((TabPaneController) controller).setTabFactory(TabFactoryEnum.getByPaneType(config.getTabName()).getTabFactory());
//            ((TabPaneController) controller).createNewTab();
//        }
//        ContentPaneGroup.contentPaneMap.put(config.getTabName(), contentPane);
//        return contentPane;
//    }

}
