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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseServer;
import org.hqu.lly.domain.bean.CustomDataConfig;
import org.hqu.lly.domain.bean.SendSettingConfig;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.domain.config.ServerConfig;
import org.hqu.lly.exception.UnSetBoundException;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.protocol.tcp.server.TCPServer;
import org.hqu.lly.protocol.udp.server.UDPServer;
import org.hqu.lly.protocol.websocket.server.WebSocketServer;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.DataUtil;
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
public abstract class BaseServerController<T> extends BaseController implements Initializable {

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
    protected ListView<MsgLabel> msgList;
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

    /**
     * 当前标签页的标题
     */
    @Setter
    protected TextField tabTitle;
    /**
     * netty服务端。<br>
     * 应为 {@link TCPServer}、{@link UDPServer}、{@link WebSocketServer}
     * 中的一个。
     */
    protected BaseServer<T> server;
    // XXX 以下两个set可能可以合二为一。
    /**
     * 要向其发送消息的地址集合(用于无连接协议)
     */
    protected Set<T> clientAddrSet = ConcurrentHashMap.newKeySet();
    /**
     * 要向其发送消息的客户端集合
     */
    protected Set<T> targetClientSet = ConcurrentHashMap.newKeySet();
    /**
     * 长文本是否换行flag
     */
    protected boolean softWrap = false;
    /**
     * 是否全选客户端flag
     */
    protected boolean selectAll = true;
    /**
     * 用于将netty接收的消息或发生的异常显示在GUI面板上
     */
    protected ServerService serverService;

    /**
     * 发送设置面板
     */
    protected Stage sendSettingPane;
    /**
     * 定时发送服务
     */
    protected ScheduledSendService scheduledService;
    /**
     * 发送设置配置类
     */
    protected SendSettingConfig sendSettingConfig = new SendSettingConfig();

    /**
     * 定时发送任务
     */
    protected ScheduledTaskService scheduledTaskService;
    /**
     * 客户端面板配置类
     */
    protected ServerConfig serverConfig;
    /**
     * 显示在 {@link #clientListBox} 中的客户端列表
     */
    ObservableList<T> clientList = FXCollections.observableArrayList();

    public BaseServerController() {
        setServer();
        setServerService();
    }

    /**
     * <p>
     * 通过本地配置文件初始化加载数据
     * </p>
     *
     * @param config 服务端配置文件类
     * @date 2023-02-06 11:34:25 <br>
     */
    public void initByConfig(ServerConfig config) {
        tabTitle.setText(config.getTabName());
        serverPort.setText(config.getPort());
        msgInput.setText(config.getMsgInput());
        sendSettingConfig = config.getSendSettingConfig();
        initSendSetting();
    }

    /**
     * <p>
     * 设置服务端实例 {@link #serverService},由子类重写。
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
     */
    protected abstract void setServer();


    /**
     * <p>
     * 设置服务端服务,主要为设置服务的生命周期回调函数。
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
     */
    protected abstract void setServerService();


    /**
     * <p>
     * 设置客户端列表的细胞工厂,由子类重写。
     * </p>
     *
     * @date 2022-10-23 21:16:35 <br>
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
        // XXX clientAddrSet似乎没有必要，待优化
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
        sendMsg();
    }

    private void sendMsg() {

        if ("未定义数据边界!".equals(errorMsgLabel.getText())) {
            errorMsgLabel.setText("");
        }

        if (!targetClientSet.isEmpty()) {
            targetClientSet.forEach((client) -> {
                String text = msgInput.getText();
                if (sendSettingConfig.isTextMode()) {
                    server.sendMessage(text, client);
                }

                try {
                    if (sendSettingConfig.isCustomMode()) {
                        CustomDataConfig customDataConfig = sendSettingConfig.getCustomDataConfig();
                        String msg = DataUtil.createMsg(customDataConfig.getCustomDataPattern(), customDataConfig.getBoundList());
                        server.sendMessage(msg, client);
                    }
                } catch (UnSetBoundException e) {
                    log.warn(e.getMessage());
                    Platform.runLater(() -> {
                        errorMsgLabel.setText("未定义数据边界!");
                    });
                }
            });
        }
    }


    @FXML
    void scheduleSend(MouseEvent event) {
        if (scheduleSendBtn.isSelected()) {
            scheduledService = new ScheduledSendService(sendSettingConfig.getScheduledSendConfig(), scheduledTaskService);
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
        ObservableList<MsgLabel> msgItems = msgList.getItems();
        msgItems.forEach(msgLabel -> msgLabel.setPrefWidth(labelWidth));
        Platform.runLater(() -> msgList.setItems(msgItems));
    }

    /**
     * <p>
     * 标签页关闭前的回调函数。<br>
     * 负责取消定时任务以及关闭服务端连接。
     * </p>
     *
     * @date 2023-02-06 12:27:58 <br>
     */
    public void destroy() {
        // 如果有定时任务正在执行,则取消
        if (scheduledService != null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        server.destroy();
        destroyed = true;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initSendSetting();
        // 初始化客户端列表盒子
        setClientBox();
        // 功能按钮悬浮tip提示
        initBtnTips();
        // 消息上下文菜单
        // msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
        clientListBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * <p>
     * 初始化存放客户端集合的列表盒子 {@link ListView}.
     * 为盒子中的客户端条目设置点击回调.
     * </p>
     *
     * @date 2023-02-06 11:30:13 <br>
     */
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

    /**
     * <p>
     * 初始化发送设置面板。<br>
     * 包括:<br>
     * 1.定时任务设置;<br>
     * 2.发送模式改变时触发的回调;<br>
     * 3.发送设置面板的创建。<br>
     * </p>
     *
     * @date 2023-02-06 10:10:24 <br>
     */
    protected void initSendSetting() {
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
        sendSettingConfig.getScheduledSendConfig().setTaskFactory(new SendTaskFactory(new TaskService() {
            @Override
            public void fireTask() {
                sendMsg();
            }
        }));

        sendSettingConfig.setOnModeChange(new TaskService() {
            @Override
            public void fireTask() {
                if (sendSettingConfig.isTextMode()) {
                    Platform.runLater(() -> {
                        msgInput.setDisable(false);
                    });
                }
                if (sendSettingConfig.isCustomMode()) {
                    Platform.runLater(() -> {
                        msgInput.setDisable(true);
                    });
                }
            }
        });

        sendSettingPane = SendSettingPaneFactory.create(sendSettingConfig);
    }

    /**
     * <p>
     * 保存并返回当前标签页的服务端配置。
     * 当前标签页的控制器应是 {@link BaseServerController}的子类。 <br>
     * 保存的服务端配置包括：<br>
     * 1.消息框中的文本;<br>
     * 2.服务端端口;<br>
     * 3.发送设置.
     * </p>
     *
     * @return {@link ServerConfig} 服务端标签页配置
     * @date 2023-02-06 11:26:53 <br>
     */
    @Override
    public ServerConfig saveAndGetConfig() {
        serverConfig = new ServerConfig();
        serverConfig.setTabName(tabTitle.getText());
        serverConfig.setMsgInput(msgInput.getText());
        serverConfig.setPort(serverPort.getText());
        serverConfig.setSendSettingConfig(sendSettingConfig);
        return serverConfig;
    }

    /**
     * <p>
     * 为界面按钮添加提示文字 {@link Tooltip}。<br>
     * </p>
     * 包括: <br>
     * 信息框侧边栏按钮：<br>
     * &emsp 1.长文本换行; <br>
     * &emsp 2.清空列表; <br>
     * &emsp 3.发送设置。<br>
     * 客户端列表侧边按钮：<br>
     * &emsp 1.全选/取消全选; <br>
     * &emsp 2.删除客户端; <br>
     *
     * @date 2023-02-06 11:02:46 <br>
     */
    protected void initBtnTips() {
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));


        selectAllBtn.setTooltip(UIUtil.getTooltip("全选/取消全选"));
        removeClientBtn.setTooltip(UIUtil.getTooltip("删除客户端"));

    }

}
