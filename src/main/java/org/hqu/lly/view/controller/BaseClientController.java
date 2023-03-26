package org.hqu.lly.view.controller;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.base.BaseClient;
import org.hqu.lly.domain.bean.CustomDataConfig;
import org.hqu.lly.domain.bean.SendSettingConfig;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.domain.config.ClientConfig;
import org.hqu.lly.domain.config.TabConfig;
import org.hqu.lly.domain.config.TopConfig;
import org.hqu.lly.exception.UnSetBoundException;
import org.hqu.lly.factory.SendSettingPaneFactory;
import org.hqu.lly.factory.SendTaskFactory;
import org.hqu.lly.service.ScheduledTaskService;
import org.hqu.lly.service.impl.ClientService;
import org.hqu.lly.service.impl.ScheduledSendService;
import org.hqu.lly.utils.DataUtil;
import org.hqu.lly.utils.UIUtil;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
public abstract class BaseClientController<T extends BaseClient> extends BaseController implements Initializable {

    private boolean showTime = true;
    protected Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 长文本换行flag
     */
    protected boolean softWrap = false;

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
    protected TextField tabTitle;

    /**
     * 客户端面板配置类
     */
    protected ClientConfig clientConfig;

    @FXML
    private TextField remoteAddressInput;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private ListView<MsgLabel> msgList;
    @FXML
    private TextArea msgInput;
    @FXML
    private Button sendMsgButton;
    @FXML
    private ToggleButton scheduleSendBtn;
    @FXML
    private ToggleButton softWrapBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button sendSettingBtn;
    private double contentWidth;

    public BaseClientController() {
        setProtocol();
        setClient();
    }

    /**
     * <p>
     * 通过本地配置文件初始化加载数据
     * </p>
     *
     * @param config 客户端配置文件类
     * @date 2023-02-05 15:18:25 <br>
     * @author hqully <br>
     */
    public void initByConfig(ClientConfig config) {
        tabTitle.setText(config.getTabName());
        remoteAddressInput.setText(config.getServerAddr());
        msgInput.setText(config.getMsgInput());
        sendSettingConfig = config.getSendSettingConfig();
        initSendSetting();
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
     * @return {@link TabConfig} 客户端标签页配置
     * @date 2023-02-06 10:50:53 <br>
     */
    @Override
    public TabConfig saveAndGetConfig() {
        clientConfig = new ClientConfig();
        clientConfig.setMsgInput(msgInput.getText());
        clientConfig.setTabName(tabTitle.getText());
        clientConfig.setServerAddr(remoteAddressInput.getText());
        clientConfig.setSendSettingConfig(sendSettingConfig);
        return clientConfig;
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
        client.setURI(uri);
        client.setService(new BaseClientService());

        FutureTask<Channel> channel = new FutureTask<>(client);
        executor.execute(channel);
        Platform.runLater(() -> {
            errorMsgLabel.setText("连接中...");
        });
    }

    @FXML
    void disconnect(MouseEvent event) {
        destroyTask();
        setInactiveUI();
    }


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

        // 普通文本模式
        String text = msgInput.getText();
        if (sendSettingConfig.isTextMode()) {
            client.sendMessage(text);
        }

        // 自定义格式模式
        try {
            if (sendSettingConfig.isCustomMode()) {
                CustomDataConfig customDataConfig = sendSettingConfig.getCustomDataConfig();
                String msg = DataUtil.createMsg(customDataConfig.getCustomDataPattern(), customDataConfig.getBoundList());
                client.sendMessage(msg);

            }
        } catch (UnSetBoundException e) {
            log.warn(e.getMessage());
            Platform.runLater(() -> {
                errorMsgLabel.setText("未定义数据边界!");
            });
        }
    }

    @FXML
    void clearMsg(MouseEvent event) {
        // 移除消息列表中的所有消息
        // msgList.getItems().forEach(item -> item.showTime(showTime));
        // showTime = !showTime;
        msgList.getItems().remove(0, msgList.getItems().size());
        msgList.refresh();
    }

    @FXML
    void handleSoftWrap(MouseEvent event) {
        softWrap = !softWrap;
        double labelWidth = softWrap ? UIUtil.getFixMsgLabelWidth(msgList.getWidth()) : Region.USE_COMPUTED_SIZE;
        ObservableList<MsgLabel> msgItems = msgList.getItems();
        msgItems.forEach(msgLabel -> msgLabel.setPrefWidth(labelWidth));
        Platform.runLater(() -> msgList.setItems(msgItems));
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

    @FXML
    void showSendSetting(MouseEvent event) {
        if (sendSettingPane == null) {
            initSendSetting();
        }
        sendSettingPane.show();
    }

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
        destroyed = true;
    }

    /**
     * <p>
     *     销毁任务,断开连接、关闭tab页时调用
     * </p>
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

    protected void setActiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(true);
            msgInput.setDisable(false);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            sendMsgButton.setDisable(false);
            scheduleSendBtn.setDisable(false);
        });
    }

    protected void setInactiveUI() {
        Platform.runLater(() -> {
            remoteAddressInput.setDisable(false);
            msgInput.setDisable(true);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            sendMsgButton.setDisable(true);
            scheduleSendBtn.setDisable(true);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // 若不是从配置文件中加载面板,
        // 则在本面板初始化时就可以初始化发送设置.
        if (!TopConfig.isLoad()) {
            // 发送设置
            initSendSetting();
        }

        // 功能按钮悬浮tip提示
        initMsgSideBar();

        // 消息上下文菜单
        // msgList.setContextMenu(UIUtil.getMsgListMenu(msgList));
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
            if (sendSettingConfig.isTextMode()) {
                Platform.runLater(() -> msgInput.setDisable(false));
            }
            if (sendSettingConfig.isCustomMode()) {
                Platform.runLater(() -> msgInput.setDisable(true));
            }
        });

        // 创建发送设置面板
        sendSettingPane = SendSettingPaneFactory.create(sendSettingConfig);
    }

    /**
     * <p>
     * 为消息框的侧边栏按钮添加提示文字 {@link Tooltip}。<br>
     * 包括：<br>
     * &emsp 1.长文本换行; <br>
     * &emsp 2.清空列表; <br>
     * &emsp 3.发送设置。<br>
     * </p>
     *
     * @date 2023-02-06 11:02:46 <br>
     */
    protected void initMsgSideBar() {
        softWrapBtn.setTooltip(UIUtil.getTooltip("长文本换行"));
        clearBtn.setTooltip(UIUtil.getTooltip("清空列表"));
        sendSettingBtn.setTooltip(UIUtil.getTooltip("发送设置"));

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

}
