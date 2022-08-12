package com.hqu.lly.view.controller;

import com.hqu.lly.constant.ContentPaneConsts;
import com.hqu.lly.service.SwitchPaneService;
import com.hqu.lly.service.impl.ContentPaneManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * 主面板
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/3 20:07
 * @Version 1.0
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
        TreeItem<String> root = new TreeItem<>("root");
        TreeItem<String> tcp = new TreeItem<>("tcp");

        LeafTreeItem<String> tcpServer = new LeafTreeItem<>("server", new ContentPaneManager(mainPane, ContentPaneConsts.TCP_SERVER_PANE,"tabPane.fxml"));

        LeafTreeItem<String> tcpClient = new LeafTreeItem<>("client",new ContentPaneManager(mainPane, ContentPaneConsts.TCP_CLIENT_PANE,"tabPane.fxml"));
        tcp.getChildren().add(tcpServer);
        tcp.getChildren().add(tcpClient);
        TreeItem<String> udp = new TreeItem<>("udp");
        TreeItem<String> webSocket = new TreeItem<>("webSocket");

        LeafTreeItem<String> server = new LeafTreeItem<>("server", new ContentPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_SERVER_PANE,"tabPane.fxml"));

        LeafTreeItem<String> client = new LeafTreeItem<>("client",new ContentPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_CLIENT_PANE,"tabPane.fxml"));

        webSocket.getChildren().add(server);
        webSocket.getChildren().add(client);

        List<TreeItem<String>> firstMenuItems = new ArrayList<>();
        firstMenuItems.add(tcp);
        firstMenuItems.add(udp);
        firstMenuItems.add(webSocket);

        int i = 0;
        firstMenuItems.forEach(item -> {

/*
            LeafTreeItem<String> server = new LeafTreeItem<>("server", new ContentPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_SERVER_PANE,"tabPane.fxml"));

            LeafTreeItem<String> client = new LeafTreeItem<>("client",new ContentPaneManager(mainPane, ContentPaneConsts.WEB_SOCKET_CLIENT_PANE,"tabPane.fxml"));

            item.getChildren().add(server);
            item.getChildren().add(client);
*/

            item.setExpanded(true);
        });


        root.getChildren().addAll(firstMenuItems);

        root.setExpanded(true);

        this.menuTree.setRoot(root);

        menuTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Object selectedItem = menuTree.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof LeafTreeItem){
                    ((LeafTreeItem<?>) selectedItem).switchPane();
                }
            }
        });
        menuTree.setShowRoot(false);
    }

    class LeafTreeItem<T> extends TreeItem{

        private SwitchPaneService switchPaneService;



        public LeafTreeItem(T name , SwitchPaneService switchPaneService) {

            super(name);
            this.switchPaneService = switchPaneService;
        }

        public void switchPane(){
            switchPaneService.switchPane();
            log.info("switch pane");
        }
    }
}
