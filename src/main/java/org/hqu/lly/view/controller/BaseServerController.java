package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseServer;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.ConfigStore;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.domain.config.SendSettingConfig;
import org.hqu.lly.domain.config.ServerSessionConfig;
import org.hqu.lly.domain.egg.Egg;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.exception.UnSetBoundException;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.protocol.tcp.server.TCPServer;
import org.hqu.lly.protocol.udp.server.UDPServer;
import org.hqu.lly.protocol.websocket.server.WebSocketServer;
import org.hqu.lly.service.MyInitialize;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.service.impl.ServerService;
import org.hqu.lly.utils.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static org.hqu.lly.enums.DataType.HEX;
import static org.hqu.lly.enums.DataType.PLAIN_TEXT;

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
public abstract class BaseServerController<T> extends CommonUIContorller implements MyInitialize<ServerSessionConfig> {

    protected boolean fromConfig = false;
    protected Executor executor = Executors.newSingleThreadExecutor();

    @FXML
    protected TextField serverPort;
    @FXML
    protected Button confirmButton;
    @FXML
    protected Label errorMsgLabel;
    @FXML
    protected Button closeServerButton;
    @FXML
    protected ListView<T> clientListBox;
    @FXML
    protected Button selectAllBtn;
    @FXML
    protected Button removeClientBtn;

    @FXML
    public ToggleButton muteResBtn;

    @Setter
    protected TitleTab tab;

    protected boolean muteRes = false;

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
     * 是否全选客户端flag
     */
    protected boolean selectAll = true;

    protected ServerService serverService;

    // region config

    /**
     * 客户端面板配置类
     */
    protected ServerSessionConfig serverConfig;
    /**
     * 发送设置配置类
     */
    protected SendSettingConfig sendSettingConfig;

    // endregion

    /**
     * 发送设置面板
     */
    protected Stage sendSettingPane;
    /**
     * 定时发送服务
     */
    protected ScheduledSendService scheduledService;

    /**
     * 定时发送任务
     */
    protected ScheduledTaskService scheduledTaskService;
    /**
     * 显示在 {@link #clientListBox} 中的客户端列表
     */
    ObservableList<T> clientList = FXCollections.observableArrayList();


    public BaseServerController() {
    }


    public void initConfig() {
        serverConfig = (ServerSessionConfig) ConfigStore.createConfig(ConfigType.SERVER);
        sendSettingConfig = serverConfig.getSendSettingConfig();
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
        Egg.egg(serverPort.getText());
        try {
            ValidateUtil.checkPort(serverPort.getText());
        } catch (IllegalArgumentException e) {
            errorMsgLabel.setText(e.getMessage());
            return;
        }
        int port = Integer.parseInt(serverPort.getText());
        server.setPort(port);
        server.setService(serverService);

        FutureTask<Channel> serverTask = new FutureTask<>(server);
        executor.execute(serverTask);
        Platform.runLater(() -> errorMsgLabel.setText("服务开启中..."));
    }

    @FXML
    void closeServer(MouseEvent event) {
        destroyTask();
        setInactiveUI();
    }

    //region client box
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
    //endregion

    //region sidebar
    @FXML
    void clearMsg(MouseEvent event) {
        msgList.getItems().remove(0, msgList.getItems().size());
        msgList.refresh();

    }

    @FXML
    public void handleMuteRes(MouseEvent event) {
        // the muteRes default value is false
        // and muteResBtn default value is unselected
        // so no need to judge if muteResBtn was being selected.
        muteRes = !muteRes;
    }
    //endregion


    //region message
    @FXML
    void sendMsg(MouseEvent event) {
        sendMsg();
    }

    private void sendMsg() {

        if ("未定义数据边界!".equals(errorMsgLabel.getText())) {
            errorMsgLabel.setText("");
        }

        if (!targetClientSet.isEmpty()) {
            // text mode
            targetClientSet.forEach((client) -> {
                String text = msgInput.getText();
                if (sendSettingConfig.isTextMode()) {
                    if (sendMsgType == HEX) {
                        server.sendMessage(MsgUtil.convertText(HEX, PLAIN_TEXT, text), client);
                    } else {
                        server.sendMessage(text, client);
                    }
                }

                // custom mode
                try {
                    if (sendSettingConfig.isCustomMode()) {
                        CustomDataConfig customDataConfig = sendSettingConfig.getCustomDataConfig();
                        String msg = DataUtil.createMsg(customDataConfig.getCustomDataPattern(), customDataConfig.getBoundList());
                        if (sendMsgType == HEX) {
                            server.sendMessage(MsgUtil.convertText(HEX, PLAIN_TEXT, msg), client);
                        } else {
                            server.sendMessage(msg, client);
                        }
                    }
                } catch (UnSetBoundException e) {
                    log.warn(e.getMessage());
                    Platform.runLater(() -> errorMsgLabel.setText("未定义数据边界!"));
                }

                // js mode
                if (sendSettingConfig.isJSMode()) {
                    Object res = JSParser.evalScript(sendSettingConfig.getJsCodeConfig().getEngine(),sendSettingConfig.getJsCodeConfig().getScript());
                    String msg = res == null ? "" : res.toString();
                    if (sendMsgType == HEX) {
                        server.sendMessage(MsgUtil.convertText(HEX, PLAIN_TEXT, msg),client);
                    } else {
                        server.sendMessage(msg,client);
                    }
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
    // endregion


    @FXML
    void showSendSetting(MouseEvent event) {
        sendSettingPane.show();
    }


    //region destroy
    /**
     * 销毁server并在做一些清理工作
     *  @date 2023-07-12 20:16
     */
    public void destroyTask() {
        // 如果有定时任务正在执行,则取消
        if (scheduledService != null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        server.destroy();
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
        destroyTask();
        ConfigStore.controllers.remove(this);
        ConfigStore.removeSessionConfig(serverConfig.getId());
    }
    //endregion


    //region init
    @FXML
    public void initialize() {
        // 初始化客户端列表盒子
        setClientBox();
        // 功能按钮悬浮tip提示
        initBtnTips();
        // 多格式设置
        setupSendFormatBtn();
        setupRecvFormatBtn();
        setupMsgList();
        // 消息上下文菜单
        msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
        clientListBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setupDisplaySetting();
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
        clientListBox.getSelectionModel().getSelectedItems().addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                c.getRemoved().forEach(targetClientSet::remove);
                targetClientSet.addAll(c.getAddedSubList());
                if (targetClientSet.isEmpty()) {
                    scheduleSendBtn.setDisable(true);
                    sendMsgButton.setDisable(true);
                } else {
                    scheduleSendBtn.setDisable(false);
                    sendMsgButton.setDisable(false);
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
        sendSettingConfig.getScheduledSendConfig().setTaskFactory(new SendTaskFactory(this::sendMsg));

        sendSettingConfig.setOnModeChange(() -> {
            if (sendSettingConfig.isTextMode() && server.isActive()) {
                Platform.runLater(() -> msgInput.setDisable(false));
            }
            if (sendSettingConfig.isCustomMode() || sendSettingConfig.isJSMode()) {
                Platform.runLater(() -> msgInput.setDisable(true));
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
     * @date 2023-07-02 19:52
     */
    @Override
    public void save() {
        serverConfig.setMsgInput(msgInput.getText());
        serverConfig.setPort(serverPort.getText());
        serverConfig.setSendSettingConfig(sendSettingConfig);
        serverConfig.setTabName(tab.getTitle());
        serverConfig.setTabOrder(tab.getTabPane().getTabs().indexOf(tab));
        serverConfig.setTabSelected(tab.isSelected());
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
        displaySettingBtn.setTooltip(UIUtil.getTooltip("显示设置"));
        muteResBtn.setTooltip(UIUtil.getTooltip("禁用回复"));

        selectAllBtn.setTooltip(UIUtil.getTooltip("全选/取消全选"));
        removeClientBtn.setTooltip(UIUtil.getTooltip("删除客户端"));

    }

    @Override
    public void init() {
    }

    @Override
    public void init(ServerSessionConfig config) {
        ConfigStore.controllers.add(this);
        if (config == null) {
            initConfig();
        } else {
            serverConfig = config;
            sendSettingConfig = config.getSendSettingConfig();
            tab.setTitle(config.getTabName());
            serverPort.setText(config.getPort());
            msgInput.setText(config.getMsgInput());
        }
        setServer();
        setServerService();
        initSendSetting();

    }
    //region init

    protected void setActiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(true);
            if (sendSettingConfig.isTextMode()){
                msgInput.setDisable(false);
            }
            confirmButton.setDisable(true);
            closeServerButton.setDisable(false);
        });
    }

    protected void setInactiveUI() {
        Platform.runLater(() -> {
            serverPort.setDisable(false);
            if (!server.isActive()){
                msgInput.setDisable(true);
            }
            confirmButton.setDisable(false);
            closeServerButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
    }

}
