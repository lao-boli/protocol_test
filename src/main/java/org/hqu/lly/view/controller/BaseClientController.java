package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.ListItemPopup;
import org.hqu.lly.domain.component.MessagePopup;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.domain.component.TitleTab;
import org.hqu.lly.domain.config.*;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.exception.UnSetBoundException;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.protocol.base.BaseClient;
import org.hqu.lly.service.MyInitialize;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.utils.DataUtil;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.MsgUtil;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static org.hqu.lly.enums.DataType.HEX;
import static org.hqu.lly.enums.DataType.PLAIN_TEXT;
import static org.hqu.lly.utils.UIUtil.getMsgListMenu;
import static org.hqu.lly.utils.UIUtil.getTooltip;

/**
 * <p>
 * 客户端控制器基类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/10/2 20:20
 */
@Slf4j
public abstract class BaseClientController<T extends BaseClient> extends CommonUIContorller implements MyInitialize<ClientSessionConfig> {

    protected Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 发送设置面板
     */
    protected Stage sendSettingPane;

    /**
     * 定时发送服务
     */
    protected ScheduledSendService scheduledService;

    // region config
    /**
     * 客户端面板配置类
     */
    protected ClientSessionConfig clientConfig;
    /**
     * 发送设置配置类
     */
    protected SendSettingConfig sendSettingConfig;

    // endregion

    /**
     * 定时任务服务
     */
    protected ScheduledTaskService scheduledTaskService;
    /**
     * 客户端服务
     */
    protected T client;
    /**
     * 当前面板的连接协议
     */
    protected String protocol;

    /**
     * 当前标签页的标题
     */

    @Setter
    protected TitleTab tab;

    @FXML
    public VBox tabContainer;

    @FXML
    private TextField remoteAddressInput;

    @FXML
    public Button addrHistoryBtn;

    @FXML
    public Button showHistoryBtn;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label errorMsgLabel;
    private ListItemPopup historyPopup;

    public BaseClientController() {
    }


    /**
     * <p>
     * 为当前controller设置协议
     * </p>
     *
     * @date 2022-10-23 18:38:41 <br>
     */
    protected abstract void setProtocol();

    /**
     * <p>
     * 为当前controller设置客户端实体
     * </p>
     *
     * @date 2022-10-23 18:38:51 <br>
     */
    protected abstract void setClient();


    @FXML
    void confirmAddr(MouseEvent event) {
        URI uri = URI.create(protocol + remoteAddressInput.getText());
        try {
            client.setURI(uri);
        } catch (IllegalArgumentException e) {
            errorMsgLabel.setText(e.getMessage());
            return;
        }
        client.setService(new BaseClientService());

        FutureTask<Channel> channel = new FutureTask<>(client);
        executor.execute(channel);
        Platform.runLater(() -> {
            errorMsgLabel.setText("连接中...");
        });
    }


    //region history
    @FXML
    public void addHistory(MouseEvent mouseEvent) {
        if (historyPopup == null) {
            setUpHistory();
        }
        historyPopup.addData(remoteAddressInput.getText());
        new MessagePopup("已添加到历史记录").showPopup(30, 0.8);
    }

    @FXML
    public void showHistory(MouseEvent mouseEvent) {
        if (historyPopup == null) {
            setUpHistory();
        }
        if (!remoteAddressInput.isFocused()) {
            remoteAddressInput.requestFocus();
        }
        historyPopup.showPopup(2, remoteAddressInput);

    }

    private void setUpHistory() {

        historyPopup = new ListItemPopup();
        historyPopup.getDataListView().prefWidthProperty().bind(remoteAddressInput.widthProperty());
        historyPopup.setOnItemClicked(s -> remoteAddressInput.setText(s));
        remoteAddressInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // if only judge remoteAddressInput if lost focus,
            // it will cause popup to appear and disappear immediately
            // so judge showHistoryBtn.isFocused() is require
            if (!newValue && !showHistoryBtn.isFocused() && !historyPopup.onWorking.get()) {
                historyPopup.close();
            }
        });
    }

    private void setUpHistory(List<String> historyList) {
        setUpHistory();
        historyPopup.addDataList(historyList);

    }

    //endregion

    @FXML
    void disconnect(MouseEvent event) {
        destroyTask();
        setInactiveUI();
    }


    //region message
    @FXML
    void sendMsg(MouseEvent event) {
        sendMsg();
    }

    /**
     * <p>
     * 根据发送模式的不同向服务端发送消息。
     * 在自定义数据模式下，若未定义数据边界，
     * 则会在 {@link #errorMsgLabel}中显示错误信息。
     * </p>
     *
     * @date 2023-02-06 10:15:24 <br>
     */
    private void sendMsg() {

        // 重置错误信息
        if ("未定义数据边界!".equals(errorMsgLabel.getText())) {
            errorMsgLabel.setText("");
        }

        String text = msgInput.getText();
        // 普通文本模式
        if (sendSettingConfig.isTextMode()) {
            handleSend(text);
        }
        // 自定义格式模式
        if (sendSettingConfig.isCustomMode()) {
            customModeSend();
        }
        // js mode
        if (sendSettingConfig.isJSMode()) {
            jsModeSend();
        }
    }

    private void customModeSend() {
        try {
            CustomDataConfig customDataConfig = sendSettingConfig.getCustomDataConfig();
            String msg = DataUtil.createMsg(customDataConfig.getCustomDataPattern(), customDataConfig.getBoundList());
            handleSend(msg);
        } catch (UnSetBoundException e) {
            log.warn(e.getMessage());
            Platform.runLater(() -> errorMsgLabel.setText("未定义数据边界!"));
        }
    }

    private void jsModeSend() {
        JSCodeConfig jsCodeConfig = sendSettingConfig.getJsCodeConfig();
        Object res = JSParser.evalScript(jsCodeConfig.getEngine(), jsCodeConfig.getScript());
        String msg = res == null ? "" : res.toString();
        handleSend(msg);
    }

    private void handleSend(String text) {
        if (sendMsgType == HEX) {
            client.sendMessage(MsgUtil.convertText(HEX, PLAIN_TEXT, text));
        } else {
            client.sendMessage(text);
        }
    }

    @FXML
    void clearMsg(MouseEvent event) {
        // 移除消息列表中的所有消息
        msgList.getItems().remove(0, msgList.getItems().size());
        msgList.refresh();
    }


    @FXML
    void scheduleSend(MouseEvent event) {
        if (scheduleSendBtn.isSelected()) {
            // 新建定时任务并开启
            scheduledService = new ScheduledSendService(sendSettingConfig.getScheduledSendConfig(), scheduledTaskService);
            scheduledService.start();
        }
        if (!scheduleSendBtn.isSelected()) {
            // 取消定时任务
            scheduledService.cancel();
        }
    }
    //endregion


    @FXML
    void showSendSetting(MouseEvent event) {
        if (sendSettingPane == null) {
            initSendSetting();
        }
        sendSettingPane.show();
    }

    //region destroy

    /**
     * <p>
     * 标签页关闭前的回调函数。<br>
     * 负责取消定时任务以及关闭客户端连接。
     * </p>
     *
     * @date 2023-02-06 10:20:12 <br>
     */
    public void destroy() {
        destroyTask();
        ConfigStore.removeSessionConfig(clientConfig.getId());
        ConfigStore.controllers.remove(this);
        destroyed = true;
    }

    /**
     * <p>
     * 销毁任务,断开连接、关闭tab页时调用
     * </p>
     *
     * @date 2023-02-24 20:22:37 <br>
     */
    public void destroyTask() {
        // 如果有定时任务正在执行,则取消
        if (scheduledService != null && scheduledService.isRunning()) {
            scheduledService.cancel();
            scheduleSendBtn.setSelected(false);
        }
        client.destroy();
    }
    //endregion

    //region init
    @FXML
    public void initialize() {
        tabContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            EventTarget target = event.getTarget();
            // target type isn't a control but a pane,
            // indicates clicking on the container background,
            // it should make every control lost focus.
            // and I don't know why when click on an area without text in textField,
            // the target type is pane rather than textfield
            // so need make a parent node validate
            if (target instanceof Pane && !(((Pane) target).getParent() instanceof TextField)) {
                tabContainer.requestFocus();
            }
        });
        // 功能按钮悬浮tip提示
        initControlToolTip();
        setupDisplaySetting();
        // 多格式设置
        setupSendFormatBtn();
        setupRecvFormatBtn();
        setupExportBtn();

        setupMsgList();

        // 消息上下文菜单
        msgList.setContextMenu(getMsgListMenu(msgList));
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
    private void initSendSetting() {
        // 定时任务设置
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

        // 发送模式改变时的回调
        sendSettingConfig.setOnModeChange(() -> {
            if (sendSettingConfig.isTextMode() && client.isActive()) {
                Platform.runLater(() -> msgInput.setDisable(false));
            }
            if (sendSettingConfig.isCustomMode() || sendSettingConfig.isJSMode()) {
                Platform.runLater(() -> msgInput.setDisable(true));
            }
        });

        // 创建发送设置面板
        // new a stage must be in FX app thread
        Platform.runLater(() -> {
            sendSettingPane = SendSettingPaneFactory.create(sendSettingConfig);
        });

    }


    /**
     * <p>
     * 为控件添加提示文字 {@link Tooltip}。<br>
     *
     * @date 2023-02-06 11:02:46 <br>
     */
    protected void initControlToolTip() {
        // msg sidebar
        softWrapBtn.setTooltip(getTooltip("长文本换行"));
        clearBtn.setTooltip(getTooltip("清空列表"));
        sendSettingBtn.setTooltip(getTooltip("发送设置"));
        displaySettingBtn.setTooltip(getTooltip("显示设置"));

        // XXX custom show position
        addrHistoryBtn.setTooltip(getTooltip("添加到历史记录"));
        showHistoryBtn.setTooltip(getTooltip("显示历史记录"));
    }

    @Override
    public void init() {

    }

    @Override
    public void init(ClientSessionConfig config) {
        ConfigStore.controllers.add(this);
        if (config == null) {
            clientConfig = (ClientSessionConfig) ConfigStore.createConfig(ConfigType.CLIENT);
            // 防止空指针
            setUpHistory();
        } else {
            clientConfig = config;
            tab.setTitle(config.getTabName());
            remoteAddressInput.setText(config.getServerAddr());
            msgInput.setText(config.getMsgInput());
            setUpHistory(config.getAddrHistoryList());
        }
        setProtocol();
        setClient();
        sendSettingConfig = clientConfig.getSendSettingConfig();
        initSendSetting();
    }
    //endregion


    protected void setActiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(true);
            addrHistoryBtn.setDisable(true);
            showHistoryBtn.setDisable(true);
            if (sendSettingConfig.isTextMode()) {
                msgInput.setDisable(false);
            }
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            sendMsgButton.setDisable(false);
            scheduleSendBtn.setDisable(false);
        });
    }

    protected void setInactiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(false);
            addrHistoryBtn.setDisable(false);
            showHistoryBtn.setDisable(false);
            msgInput.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
    }

    private class BaseClientService extends ClientService {

        @Override
        public void onStart() {
            if (!errorMsgLabel.getText().isEmpty()) {
                Platform.runLater(() -> errorMsgLabel.setText(""));
            }
            setActiveUI();
        }

        @Override
        public void onError(Throwable e, String errorMessage) {
            Platform.runLater(() -> {
                if (errorMessage != null) {
                    errorMsgLabel.setText(errorMessage);
                } else {
                    errorMsgLabel.setText(e.getMessage());
                }
            });
        }

        @Override
        public void onClose() {
            client.destroy();
            setInactiveUI();
        }

        @Override
        public void updateMsgList(MsgLabel msg) {
            Platform.runLater(() -> msgList.getItems().add(msg));
        }

    }

    /**
     * <p>
     * 保存并返回当前标签页的客户端配置。
     * 当前标签页的控制器应是 {@link BaseClientController}的子类。 <br>
     * 保存的客户端配置包括：<br>
     * 1.消息框中的文本;<br>
     * 2.连接的服务端地址;<br>
     * 3.发送设置.
     * </p>
     *
     * @date 2023-07-05 20:15
     */
    @Override
    public void save() {
        clientConfig.setMsgInput(msgInput.getText());
        clientConfig.setServerAddr(remoteAddressInput.getText());
        clientConfig.setAddrHistoryList(historyPopup.getDataList());
        clientConfig.setSendSettingConfig(sendSettingConfig);
        clientConfig.setTabName(tab.getTitle());
        clientConfig.setTabOrder(tab.getTabPane().getTabs().indexOf(tab));
        clientConfig.setTabSelected(tab.isSelected());
    }

}
