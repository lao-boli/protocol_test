package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.MyAlert;
import org.hqu.lly.domain.component.ServiceItem;
import org.hqu.lly.domain.config.ConfigStore;
import org.hqu.lly.domain.config.SessionConfig;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.service.impl.TabPaneManager;
import org.hqu.lly.utils.UIUtil;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static org.hqu.lly.enums.PaneType.*;

/**
 * <p>
 * 应用程序主面板
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/3 20:07
 */
@Slf4j
public class MainController implements Initializable {

    @FXML
    private SplitPane mainSplitPane;
    /**
     * 侧边栏菜单树
     */
    @FXML
    private TreeView<String> menuTree;
    @FXML
    private BorderPane titleBar;
    @FXML
    private TitleBarController titleBarController;
    /**
     * 主面板节点
     */
    @FXML
    private VBox mainPane;

    private final Map<PaneType, TabPaneManager> managerMap = new HashMap<>(6);

    /**
     * 侧边栏菜单宽度
     */
    private double menuWidth;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        titleBarController.init("协议测试工具", true);
        titleBarController.setTitleIcon(new Image(ResLoc.APP_ICON_64.toExternalForm()));

        titleBarController.setOnBeforeClose(() -> {
            MyAlert myAlert = new MyAlert(Alert.AlertType.CONFIRMATION, "保存配置", "是否保存配置到本地?");
            myAlert.initOwner(UIUtil.getPrimaryStage());
            Optional<ButtonType> result = myAlert.showAndWait();

            if (result.get().equals(ButtonType.OK)) {
                ConfigStore.save();
            }

            return result.get().equals(ButtonType.OK) || result.get().equals(ButtonType.CANCEL);
        });

        titleBarController.setOnClose(() -> System.exit(0));

        initSideBar();

        setupSpiltPane();

        try {
            ConfigStore.load();
            initByConfig();
        } catch (FileNotFoundException e) {
            log.warn("miss match config file");
        }
    }

    /**
     * 设置 {@link #mainSplitPane} ,让分割线在窗口的大小改变时保持原来的位置
     *
     * @date 2023-03-30 21:12
     */
    private void setupSpiltPane() {
        // 当FXML文件中的节点被加载时，它们的Skin尚未完全初始化。
        // 解决这个问题的一种方法是将对Skin的操作延迟到稍后的时间点。
        Platform.runLater(() -> {
            // 初始化设置
            menuWidth = menuTree.getWidth();
            // 获取分割线节点
            Node divider = mainSplitPane.lookup(".split-pane > .split-pane-divider");
            // 在拖拽时记录宽度
            divider.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> menuWidth = menuTree.getWidth());

            // stage窗口改变时保持分割线位置不变
            Stage stage = UIUtil.getPrimaryStage();
            stage.widthProperty().addListener((observable, oldValue, newValue) -> {
                mainSplitPane.setDividerPosition(0, menuWidth / newValue.doubleValue());
            });
        });

    }

    /**
     * <p>
     * 从本地存档配置文件加载面板
     * </p>
     *
     * @date 2023-02-04 18:35:07 <br>
     */
    private void initByConfig() {
        Map<String, SessionConfig> configs = ConfigStore.getSessionConfigs();
        configs.values().stream()
                .sorted((o1, o2) -> o1.getTabOrder() - o2.getTabOrder())
                .forEachOrdered(c -> {
                    log.info(c.toString());
                    switch (c.getPaneType()) {
                        case TCP_SERVER -> managerMap.get(TCP_SERVER).initAndCreateTab(c);
                        case TCP_CLIENT -> managerMap.get(TCP_CLIENT).initAndCreateTab(c);
                        case UDP_SERVER -> managerMap.get(UDP_SERVER).initAndCreateTab(c);
                        case UDP_CLIENT -> managerMap.get(UDP_CLIENT).initAndCreateTab(c);
                        case WS_SERVER -> managerMap.get(WS_SERVER).initAndCreateTab(c);
                        case WS_CLIENT -> managerMap.get(WS_CLIENT).initAndCreateTab(c);
                    }
                });
        log.info("init pane successful");

    }

    /**
     * <p>
     * 初始化主界面侧边栏
     * </p>
     *
     * @date 2023-02-06 16:07:38 <br>
     */
    private void initSideBar() {
        TreeItem<String> root = new TreeItem<>("root");

        // tcp
        TreeItem<String> tcp = new TreeItem<>("tcp");

        ServiceItem<String> tcpServer = getServiceItem(TCP_SERVER, "server");
        ServiceItem<String> tcpClient = getServiceItem(TCP_CLIENT, "client");

        tcp.getChildren().add(tcpServer);
        tcp.getChildren().add(tcpClient);

        // udp
        TreeItem<String> udp = new TreeItem<>("udp");

        ServiceItem<String> udpServer = getServiceItem(UDP_SERVER, "server");
        ServiceItem<String> udpClient = getServiceItem(UDP_CLIENT, "client");

        udp.getChildren().add(udpServer);
        udp.getChildren().add(udpClient);

        // websocket
        TreeItem<String> webSocket = new TreeItem<>("webSocket");

        ServiceItem<String> server = getServiceItem(WS_SERVER, "server");
        ServiceItem<String> client = getServiceItem(WS_CLIENT, "client");

        webSocket.getChildren().add(server);
        webSocket.getChildren().add(client);

        // 第一层菜单选项
        List<TreeItem<String>> firstMenuItems = new ArrayList<>();
        firstMenuItems.add(tcp);
        firstMenuItems.add(udp);
        firstMenuItems.add(webSocket);

        // 默认展开
        firstMenuItems.forEach(item -> item.setExpanded(true));

        root.getChildren().addAll(firstMenuItems);
        root.setExpanded(true);

        menuTree.setRoot(root);
        // 设置切换回调
        menuTree.setOnMouseClicked(mouseEvent -> {
            Object selectedItem = menuTree.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof ServiceItem) {
                ((ServiceItem<?>) selectedItem).switchPane();
            }
        });
        // 隐藏根节点
        menuTree.setShowRoot(false);
    }

    /**
     * <p>
     * 获取{@link ServiceItem}
     * </p>
     *
     * @param paneName    标签面板名称,应为 {@link ContentPaneConsts}中的值。
     * @param subPaneName 标签页名称
     * @return {@link ServiceItem}
     * @date 2023-02-06 16:08:05 <br>
     */
    private ServiceItem<String> getServiceItem(PaneType paneName, String subPaneName) {
        TabPaneManager paneManager = new TabPaneManager(mainPane, paneName);
        managerMap.put(paneName, paneManager);
        return new ServiceItem<>(subPaneName, paneManager);
    }

}
