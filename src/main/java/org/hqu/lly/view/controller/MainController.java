package org.hqu.lly.view.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.domain.vo.ServiceItem;
import org.hqu.lly.service.impl.TabPaneManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * 应用程序主面板
 * <p>
 *
 * @author liulingyu
 * @version 1.0
 * @date 2022/8/3 20:07
 */
@Slf4j
public class MainController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab createTab;

    @FXML
    private TreeView<String> menuTree;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private AnchorPane mainPane;

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initSideBar();
    }

    private void initSideBar() {
        String contentPanePath = "views/TabPane.fxml";

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
