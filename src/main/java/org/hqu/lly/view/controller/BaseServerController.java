package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseServer;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.UIUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * <p>
 * 服务端基础控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/10/3 10:45
 */
@Slf4j
public abstract class BaseServerController<T> implements Initializable {

    protected Executor executor = Executors.newSingleThreadExecutor();

    @FXML
    protected TextField serverPort;
    @FXML
    protected Button confirmButton;
    @FXML
    protected Label errorMsgLabel;
    @FXML
    protected TextArea msgInput;
    @FXML
    protected Button sendMsgButton;
    @FXML
    protected ToggleButton scheduleSendBtn;
    @FXML
    protected Button closeServerButton;
    @FXML
    protected ListView<Label> msgList;
    @FXML
    protected ListView<T> clientListBox;
    @FXML
    protected ToggleButton softWrapBtn;
    @FXML
    protected Button clearBtn;
    @FXML
    protected Button sendSettingBtn;

    @FXML
    protected Button selectAllBtn;

    @FXML
    protected Button removeClientBtn;

    protected BaseServer<T> server;
    protected Set<T> clientAddrSet = ConcurrentHashMap.newKeySet();
    protected Set<T> targetClientSet = ConcurrentHashMap.newKeySet();
    protected boolean softWrap = false;
    protected boolean selectAll = true;
    protected ServerService serverService;

    protected Stage sendSettingPane;
    protected ScheduledSendService scheduledService;
    protected ScheduledSendConfig sendConfig = new ScheduledSendConfig();
    protected ScheduledTaskService scheduledTaskService;
    ObservableList<T> clientList = FXCollections.observableArrayList();

    public BaseServerController() {
        setServer();
        setServerService();
    }

    /**
     * <p>
     * 设置服务端实例
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
     * @author hqully <br>
     */
    protected abstract void setServer();


    /**
     * <p>
     * 设置服务端服务
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
     * @author hqully <br>
     */
    protected abstract void setServerService();


    /**
     * <p>
     * 设置客户端列表的细胞工厂
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
     * @author hqully <br>
     */
    protected abstract void setClientBoxCellFactory();

    @FXML
    void startServer(MouseEvent event) {
        int port = Integer.parseInt(serverPort.getText());
        server.setPort(port);
        server.setService(serverService);

        FutureTask<Channel> serverTask = new FutureTask<>(server);
        executor.execute(serverTask);
        Platform.runLater(() -> {
            errorMsgLabel.setText("服务开启中...");
        });
    }

    @FXML
    void closeServer(MouseEvent event) {
        destroy();
        setInactiveUI();
    }

    @FXML
    void removeClient(MouseEvent event) {
        ObservableList<T> removeItems = clientListBox.getSelectionModel().getSelectedItems();
        clientAddrSet.removeAll(removeItems);
        clientList.removeAll(removeItems);
    }

    @FXML
    void selectAllClient(MouseEvent event) {
        if (selectAll) {
            clientListBox.getSelectionModel().selectAll();
        } else {
            clientListBox.getSelectionModel().clearSelection();
        }
        selectAll = !selectAll;
    }

    protected void setActiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(true);
            msgInput.setDisable(false);
            confirmButton.setDisable(true);
            closeServerButton.setDisable(false);
        });
    }

    protected void setInactiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(false);
            msgInput.setDisable(true);
            confirmButton.setDisable(false);
            closeServerButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
    }

    @FXML
    void sendMsg(MouseEvent event) {
        if (!targetClientSet.isEmpty()) {
            targetClientSet.forEach((client) -> {
                server.sendMessage(msgInput.getText(), client);
            });
        }
    }

    @FXML
    void scheduleSend(MouseEvent event) {
        if (scheduleSendBtn.isSelected()) {
            scheduledService = new ScheduledSendService(sendConfig, scheduledTaskService);
            scheduledService.start();
        }
        if (!scheduleSendBtn.isSelected()) {
            scheduledService.cancel();
        }
    }

    @FXML
    void showSendSetting(MouseEvent event) {
        sendSettingPane.show();
    }

    @FXML
    void clearMsg(MouseEvent event) {
        msgList.getItems().remove(0, msgList.getItems().size());
        msgList.refresh();

    }

    @FXML
    void handleSoftWrap(MouseEvent event) {
        softWrap = !softWrap;
        double labelWidth = softWrap ? msgList.getWidth() - 20 : Region.USE_COMPUTED_SIZE;
        ObservableList<Label> msgItems = msgList.getItems();
        msgItems.forEach(msg -> {
            UIUtil.changeMsgLabel(msg, labelWidth, softWrap);
        });
        Platform.runLater(() -> {
            msgList.setItems(msgItems);
        });
    }

    public void destroy() {
        if (scheduledService != null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        server.destroy();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initScheduleSetting();
        setClientBox();
        // 功能按钮悬浮tip提示
        initBtnTips();
        // 消息上下文菜单
        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
        clientListBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    protected void setClientBox() {
        setClientBoxCellFactory();
        // 点击时将当前的client设置为选中的client
        clientListBox.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> c) {
                while (c.next()) {
                    targetClientSet.removeAll(c.getRemoved());
                    targetClientSet.addAll(c.getAddedSubList());
                    if (targetClientSet.isEmpty()) {
                        scheduleSendBtn.setDisable(true);
                        sendMsgButton.setDisable(true);
                    } else {
                        scheduleSendBtn.setDisable(false);
                        sendMsgButton.setDisable(false);
                    }
                }
            }
        });
    }

    protected void initScheduleSetting() {
        scheduledTaskService = new ScheduledTaskService() {
            @Override
            public void onTaskStart() {
                scheduleSendBtn.setDisable(false);
            }

            @Override
            public void onAllTasksCompleted() {
                scheduleSendBtn.setSelected(false);
            }
        };
        sendConfig.setTaskFactory(new SendTaskFactory(new TaskService() {
            @Override
            public void fireTask() {
                if (!targetClientSet.isEmpty()) {
                    targetClientSet.forEach((client) -> {
                        server.sendMessage(msgInput.getText(), client);
                    });
                }
            }
        }));

        sendSettingPane = SendSettingPaneFactory.create(sendConfig);
    }

    protected void initBtnTips() {
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));


        selectAllBtn.setTooltip(UIUtil.getTooltip("全选/取消全选"));
        removeClientBtn.setTooltip(UIUtil.getTooltip("删除客户端"));

    }

}
