package org.hqu.lly.domain.component;

import io.netty.util.CharsetUtil;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
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

    private BooleanProperty wrapText;

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

    /**
     * 普通消息构造器
     *
     * @param msg 消息
     * @date 2023-03-25 21:27
     */
    public MsgLabel(String msg) {
        super();
        initTimeText();
        this.hostText = new Text();
        this.lengthText = new Text();
        initMsgText(msg);
        this.getChildren().addAll(this.timeText, this.hostText, this.lengthText, this.msgText);
    }

    /**
     * 网络消息构造器
     *
     * @param type 消息类型 应为 {@link Type}中的值
     * @param host 消息收发地址
     * @param msg  消息内容
     */
    public MsgLabel(Type type, String host, String msg) {
        super();
        this.type = type;
        initTimeText();
        initHostText(host);
        initLengthText(msg);
        initMsgText(msg);
        this.getChildren().addAll(this.timeText, this.hostText, this.lengthText, this.msgText);
    }

    private void initTimeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String time = LocalDateTime.now().format(formatter) + " ";
        this.timeText = new Text(time);
    }

    private void initHostText(String host) {
        StringBuilder sb = new StringBuilder();
        if (Type.SEND.equals(type)) {
            sb.append("---> ");
        }
        if (Type.RECEIVE.equals(type)) {
            sb.append("<--- ");
        }
        sb.append(host).append(" :");
        this.hostText = new Text(sb.toString());
    }

    private void initMsgText(String msg) {
        this.msgText = new Text(msg);
        msgBytes = msg.getBytes(CharsetUtil.UTF_8);
    }

    public String getFullText() {
        return timeText.getText() + hostText.getText() + lengthText.getText() + msgText.getText();
    }

    private void initLengthText(String msg) {
        this.lengthText = new Text("[" + CommonUtil.getRealLength(msg) + "字节] ");
    }

    public void showTime(boolean showTime) {
        timeText.setVisible(showTime);
        timeText.setManaged(showTime);
    }

    public void showLength(boolean showLength) {
        lengthText.setVisible(showLength);
        lengthText.setManaged(showLength);
    }

    public void showHost(boolean showHost) {
        hostText.setVisible(showHost);
        hostText.setManaged(showHost);
    }

    public void showMsg(boolean showMsg) {
        msgText.setVisible(showMsg);
        msgText.setManaged(showMsg);
    }

    public void showWarn(boolean show) {
        if (warn == null) {
            initWarn();
            this.getChildren().add(0, warn);
        }
        warn.setVisible(show);
        warn.setManaged(show);
    }

    /**
     * 将消息字节数组转换成 {@link DataType}中的各种格式.
     *
     * @param to 格式类型
     * @date 2023-04-03 21:20
     * @since 0.2.0
     */
    public void convertTo(DataType to) {
        showWarn(false);

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
                    showWarn(true);
                    yield result;
                }
            }
            case JSON -> {
                try {
                    yield jsonFormat(result);
                } catch (Exception e) {
                    // 非法json字符就将其当做plainText返回
                    log.warn(e.toString());
                    showWarn(true);
                    warnTip.setText("转码Json失败,\n数据将以普通文本显示");
                    yield result;
                }
            }
        };
        msgText.setText(result);
    }

    /**
     * 初始化警告图标
     *
     * @date 2023-04-04 19:18
     * @since 0.2.0
     */
    public void initWarn() {
        warn = new Label();
        warn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        warn.setGraphic(new WarnIcon());
        warn.setStyle("-fx-padding: 0 5 0 0");

        UIUtil.setTooltip(warn, warnTip,event -> {
            // 设置提示显示位置
            Bounds bounds = warn.localToScreen(warn.getBoundsInLocal());
            warnTip.show(warn, bounds.getMinX(), bounds.getMinY() - 40);
        });

    }

}
