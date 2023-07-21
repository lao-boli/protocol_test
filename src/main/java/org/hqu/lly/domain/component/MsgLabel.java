package org.hqu.lly.domain.component;

import io.netty.util.CharsetUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.enums.DataType;
import org.hqu.lly.icon.WarnIcon;
import org.hqu.lly.utils.CommonUtil;
import org.hqu.lly.utils.UIUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hqu.lly.utils.MsgUtil.*;

/**
 * <p>
 * 网络消息文本
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/25 16:03
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MsgLabel extends TextFlow {


    /**
     * 消息类型枚举
     */
    public enum Type {
        /**
         * 接收消息类型
         */
        RECEIVE,
        /**
         * 发送消息类型
         */
        SEND,
        /**
         * 普通消息类型
         */
        COMMON
    }

    /**
     * listview的msgLabel所在cell
     */
    private ListCell cell;

    private Text timeText;

    private Text hostText;

    private Text lengthText;

    private Text msgText;

    private Type type;

    /**
     * 原始消息字节数组
     */
    private byte[] msgBytes;

    private Label warn;

    private Tooltip warnTip = new Tooltip("");

    //region properties
    private BooleanProperty showTime;
    private BooleanProperty showLength;
    private BooleanProperty showHost;
    private BooleanProperty showMsg;
    private BooleanProperty showWarn;

    private ObjectProperty<DataType> toType;
    //endregion

    private DataType curType = DataType.PLAIN_TEXT;

    /**
     * 普通消息构造器
     *
     * @param msg 消息
     * @date 2023-03-25 21:27
     */
    public MsgLabel(String msg) {
        this(Type.COMMON);
        initTimeText();
        initMsgText(msg);
    }

    /**
     * 网络消息构造器
     *
     * @param type 消息类型 应为 {@link Type}中的值
     * @param host 消息收发地址
     * @param msg  消息内容
     */
    public MsgLabel(Type type, String host, String msg) {
        this(type);
        initTimeText();
        initHostText(host);
        initLengthText(msg);
        initMsgText(msg);
    }

    private MsgLabel(Type type) {
        super();
        this.type = type;
        this.timeText = new Text();
        this.hostText = new Text();
        this.lengthText = new Text();
        this.msgText = new Text();
        this.getChildren().addAll(this.timeText, this.hostText, this.lengthText, this.msgText);
        showTime = new SimpleBooleanProperty(true);
        showHost = new SimpleBooleanProperty(true);
        showLength = new SimpleBooleanProperty(true);
        showMsg = new SimpleBooleanProperty(true);
        showWarn = new SimpleBooleanProperty(false);
        toType = new SimpleObjectProperty<>(DataType.PLAIN_TEXT);
        toType.addListener((observable, oldValue, newValue) -> {
            convertTo(newValue);

        });
        setOnShowChange();
    }

    public String getText(boolean time, boolean host, boolean length, boolean msg) {
        StringBuilder sb = new StringBuilder();
        if (time){
            sb.append(timeText.getText());
        }
        if (host){
            sb.append(hostText.getText());
        }
        if (length){
            sb.append(lengthText.getText());
        }
        if (msg){
            sb.append(msgText.getText());
        }
        return sb.toString();
    }

    public String getText() {
        return getText(showTime.get(), showHost.get(), showLength.get(),showMsg.get());
    }

    public void bindFlag(BooleanProperty time, BooleanProperty host, BooleanProperty length, BooleanProperty msg) {
        showTime.bind(time);
        showHost.bind(host);
        showLength.bind(length);
        showMsg.bind(msg);
    }

    public void setCell(ListCell cell) {
        this.cell = cell;

        convertTo(toType.get());
        if (showWarn.get()){
            cell.setGraphic(warn);
        }else {
            // must be done, or cause strange display issue
            cell.setGraphic(null);
        }
    }

    private void setOnShowChange() {
        // show flag 改变时重设cell的text
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> cell.setText(getText());
        showLength.addListener(listener);
        showTime.addListener(listener);
        showHost.addListener(listener);
        showMsg.addListener(listener);

        showWarn.addListener((observable, oldValue, newValue) -> {
            if (warn == null) {
                initWarn();
            }
            if (newValue){
                if (cell != null){
                    cell.setGraphic(warn);
                }
            }else {
                cell.setGraphic(null);
            }
        });
    }

    // region init text
    private void initTimeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String time = LocalDateTime.now().format(formatter) + " ";
        this.timeText.setText(time);
    }

    private void initHostText(String host) {
        if (host == null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (Type.SEND.equals(type)) {
            sb.append("---> ");
        }
        if (Type.RECEIVE.equals(type)) {
            sb.append("<--- ");
        }
        sb.append(host).append(" :");
        this.hostText.setText(sb.toString());
    }

    private void initLengthText(String msg) {
        this.lengthText.setText("[" + CommonUtil.getRealLength(msg) + "字节] ");
    }

    private void initMsgText(String msg) {
        this.msgText.setText(msg);
        msgBytes = msg.getBytes(CharsetUtil.UTF_8);
    }
    // endregion

    /**
     * 初始化警告图标
     *
     * @date 2023-04-04 19:18
     * @since 0.2.0
     */
    private void initWarn() {
        warn = new Label();
        warn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        warn.setGraphic(new WarnIcon());
        warn.setStyle("-fx-padding: 0 5 0 0");

        UIUtil.setTooltip(warn, warnTip, event -> {
            // 设置提示显示位置
            Bounds bounds = warn.localToScreen(warn.getBoundsInLocal());
            warnTip.show(warn, bounds.getMinX(), bounds.getMinY() - 40);
        });

    }

    public String getFullText() {
        return timeText.getText() + hostText.getText() + lengthText.getText() + msgText.getText();
    }

    /**
     * 将消息字节数组转换成 {@link DataType}中的各种格式.<br>
     * 并设置cell中的text.
     *
     * @param to 格式类型
     * @date 2023-04-03 21:20
     * @since 0.2.0
     */
    public void convertTo(DataType to) {
        if (curType.equals(to)) {
            return;
        }

        // start convert
        showWarn.setValue(false);

        String result = new String(msgBytes, CharsetUtil.UTF_8);
        result = switch (to) {
            case PLAIN_TEXT -> result;
            case HEX -> byteToHex(msgBytes);
            case BASE64 -> {
                try {
                    yield decodeBase64(msgBytes);
                } catch (IllegalArgumentException e) {
                    // 非法base64字符就将其当做plainText返回
                    log.warn(e.toString());
                    warnTip.setText("转码Base64失败,\n数据将以普通文本显示");
                    showWarn.setValue(true);
                    yield result;
                }
            }
            case JSON -> {
                try {
                    yield jsonFormat(result);
                } catch (Exception e) {
                    // 非法json字符就将其当做plainText返回
                    log.warn(e.toString());
                    showWarn.setValue(true);
                    warnTip.setText("转码Json失败,\n数据将以普通文本显示");
                    yield result;
                }
            }
        };
        //end convert

        curType = to;
        msgText.setText(result);
        if (cell != null){
            cell.setText(getText());
        }
    }

}
