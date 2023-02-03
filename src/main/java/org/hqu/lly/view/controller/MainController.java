package org.hqu.lly.view.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.constant.StageConsts;
import org.hqu.lly.domain.config.TopConfig;
import org.hqu.lly.domain.vo.ServiceItem;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.TabPaneManager;
import org.hqu.lly.view.group.ControllerGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private TreeView<String> menuTree;
    @FXML
    private BorderPane titleBar;
    @FXML
    private TitleBarController titleBarController;
    @FXML
    private AnchorPane mainPane;

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        TopConfig.load();
        titleBarController.initTitleBar(StageConsts.MAIN_PANE);
        titleBarController.setOnBeforeClose(new TaskService() {
            @Override
            public void fireTask() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("save config");
                alert.setHeaderText("save config?");
                alert.setContentText("Are you ok with this?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    log.info("ok");

                    for (TabPaneController controller : ControllerGroup.tabPaneControllerSet) {
                        TopConfig.getInstance().addTabPaneConfig(controller.saveAndGetConfig());
                    }
                    TopConfig.getInstance().save();

                } else {
                    // ... user chose CANCEL or closed the dialog
                    log.info("cancel");
                }
            }
        });
        initSideBar();
    }

    private void initByConfig(){

    }
    private void initSideBar() {
        TreeItem<String> root = new TreeItem<>("root");

        TreeItem<String> tcp = new TreeItem<>("tcp");
        ServiceItem<String> tcpServer = new ServiceItem<>("server", new TabPaneManager(mainPane, ContentPaneConsts.TCP_SERVER_PANE));
        ServiceItem<String> tcpClient = new ServiceItem<>("client", new TabPaneManager(mainPane, ContentPaneConsts.TCP_CLIENT_PANE));
        tcp.getChildren().add(tcpServer);
        tcp.getChildren().add(tcpClient);

        TreeItem<String> udp = new TreeItem<>("udp");
        ServiceItem<String> udpServer = new ServiceItem<>("server", new TabPaneManager(mainPane, ContentPaneConsts.UDP_SERVER_PANE));
        ServiceItem<String> udpClient = new ServiceItem<>("client", new TabPaneManager(mainPane, ContentPaneConsts.UDP_CLIENT_PANE));
        udp.getChildren().add(udpServer);
        udp.getChildren().add(udpClient);

        TreeItem<String> webSocket = new TreeItem<>("webSocket");
        ServiceItem<String> server = new ServiceItem<>("server", new TabPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_SERVER_PANE));
        ServiceItem<String> client = new ServiceItem<>("client", new TabPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_CLIENT_PANE));
        webSocket.getChildren().add(server);
        webSocket.getChildren().add(client);

        List<TreeItem<String>> firstMenuItems = new ArrayList<>();
        firstMenuItems.add(tcp);
        firstMenuItems.add(udp);
        firstMenuItems.add(webSocket);

        firstMenuItems.forEach(item -> {
            item.setExpanded(true);
        });

        root.getChildren().addAll(firstMenuItems);
        root.setExpanded(true);

        menuTree.setRoot(root);
        menuTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Object selectedItem = menuTree.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof ServiceItem) {
                    ((ServiceItem<?>) selectedItem).switchPane();
                }
            }
        });
        menuTree.setShowRoot(false);
    }

}
