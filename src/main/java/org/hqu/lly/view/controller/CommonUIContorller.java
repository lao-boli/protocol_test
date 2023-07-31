package org.hqu.lly.view.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MessagePopup;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.enums.DataType;
import org.hqu.lly.utils.MsgUtil;
import org.hqu.lly.utils.UIUtil;

import static org.hqu.lly.enums.DataType.*;

/**
 * <p>
 * 共有UI控件控制器
 * <p>
 *
 * @author hqully
 * @date 2023/3/27 21:31
 * @since 0.1.0
 */
@Slf4j
public abstract class CommonUIContorller extends BaseController {

    /**
     * 接收消息的格式类型
     */
    protected ObjectProperty<DataType> recvMsgType = new SimpleObjectProperty<>(PLAIN_TEXT);

    protected DataType sendMsgType = PLAIN_TEXT;

    // region msg sidebar

    @FXML
    protected ToggleButton softWrapBtn;
    @FXML
    protected Button clearBtn;
    @FXML
    protected Button displaySettingBtn;

    // endregion

    //region msg list relative

    /**
     * 长文本是否换行flag
     */
    protected BooleanProperty softWrap = new SimpleBooleanProperty(false);
    @FXML
    protected Button recvFormatBtn;
    @FXML
    protected ListView<MsgLabel> msgList;
    //endregion

    // region send relative

    @FXML
    protected Button sendSettingBtn;
    @FXML
    protected Button sendFormatBtn;
    @FXML
    protected TextArea msgInput;
    @FXML
    protected Button sendMsgButton;
    @FXML
    protected ToggleButton scheduleSendBtn;
    //endregion


    /**
     * 设置消息显示设置按钮 {@link #displaySettingBtn} 的contextMenu.
     * 包括时间、主机、消息长度、消息内容的显示和隐藏.
     *
     * @date 2023-03-27 19:25
     */
    protected void setupDisplaySetting() {
        // 修改msgList中的内容时要刷新列表,否则会导致奇怪的显示问题.
        ChangeListener<Boolean> refreshList = (observable, oldValue, newValue) -> msgList.refresh();

        RadioMenuItem time = new RadioMenuItem("时间");
        time.setSelected(true);
        time.selectedProperty().addListener(refreshList);

        RadioMenuItem host = new RadioMenuItem("主机");
        host.setSelected(true);
        host.selectedProperty().addListener(refreshList);

        RadioMenuItem length = new RadioMenuItem("消息长度");
        length.setSelected(true);
        length.selectedProperty().addListener(refreshList);

        RadioMenuItem msg = new RadioMenuItem("消息内容");
        msg.setSelected(true);
        msg.selectedProperty().addListener(refreshList);

        ContextMenu contextMenu = new ContextMenu(time, host, length, msg);
        displaySettingBtn.setContextMenu(contextMenu);
        displaySettingBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            contextMenu.show(displaySettingBtn, Side.BOTTOM, 0, 0);
        });

        // 进行消息类型转换时要刷新列表,否则会导致奇怪的显示问题.
        recvMsgType.addListener((observable, oldValue, newValue) -> {
            msgList.refresh();
        });

        msgList.getItems().addListener((ListChangeListener<MsgLabel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(label -> {
                        label.bindFlag(time.selectedProperty(),host.selectedProperty(),length.selectedProperty(),msg.selectedProperty());
                        label.getToType().bind(recvMsgType);
                    });
                }
            }
        });
    }


    /**
     * 初始化接收消息格式化按钮
     *
     * @date 2023-04-03 19:01
     * @since 0.2.0
     */
    protected void setupRecvFormatBtn() {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            DataType from = (DataType) oldValue.getUserData();
            DataType to = (DataType) newValue.getUserData();
            recvMsgType.setValue(to);
        });
        setupFormatBtn(toggleGroup, recvFormatBtn);
    }

    /**
     * 初始化发送格式化按钮
     *
     * @date 2023-04-03 19:01
     * @since 0.2.0
     */
    protected void setupSendFormatBtn() {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            DataType from = (DataType) oldValue.getUserData();
            DataType to = (DataType) newValue.getUserData();
            String converted;
            try {
                converted = MsgUtil.convertText(from, to, msgInput.getText());
                sendMsgType = to;
            } catch (Exception e) {
                log.warn(e.toString());
                // json格式转换出错时,保存原本的选项
                converted = msgInput.getText();
                toggleGroup.selectToggle(oldValue);
                sendMsgType = from;

                new MessagePopup(MessagePopup.Type.ERROR,e.toString()).showPopup();
            }
            msgInput.setText(converted);
        });
        setupFormatBtn(toggleGroup, sendFormatBtn);
    }


    protected void setupMsgList() {

        msgList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MsgLabel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    wrapTextProperty().bind(softWrap);
                    wrapTextProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            // set fix width, it will autofill listview, I don't know why,but it works well.
                            setMinWidth(100);
                            setMaxWidth(100);
                            setPrefWidth(100);
                        } else {
                            // set computed width
                            setMinWidth(-1);
                            setMaxWidth(-1);
                            setPrefWidth(-1);
                        }

                    });
                    item.setCell(this);
                    setText(item.getText());
                }
            }
        });
    }


    /**
     * 初始化文本格式化按钮
     *
     * @param toggleGroup 单选菜单组
     * @param formatBtn   {@link #sendFormatBtn} 或 {@link #recvFormatBtn}
     * @date 2023-04-03 19:00
     * @since 0.2.0
     */
    private void setupFormatBtn(ToggleGroup toggleGroup, Button formatBtn) {
        RadioMenuItem plainText = new RadioMenuItem("plain text");
        plainText.setUserData(PLAIN_TEXT);
        plainText.setSelected(true);

        RadioMenuItem hex = new RadioMenuItem("Hex");
        hex.setUserData(HEX);

        RadioMenuItem base64 = new RadioMenuItem("Base64");
        base64.setUserData(BASE64);

        RadioMenuItem json = new RadioMenuItem("JSON");
        json.setUserData(JSON);

        toggleGroup.getToggles().addAll(plainText, hex, base64, json);

        ContextMenu contextMenu = new ContextMenu(plainText, hex, base64, json);
        formatBtn.setContextMenu(contextMenu);
        formatBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            contextMenu.show(formatBtn, Side.RIGHT, 0, 0);
        });

        formatBtn.setTooltip(UIUtil.getTooltip("编码设置"));
    }

    @FXML
    protected void handleSoftWrap(MouseEvent event) {
        softWrap.setValue(!softWrap.get());
    }

}
