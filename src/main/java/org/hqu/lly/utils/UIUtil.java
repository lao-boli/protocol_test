package org.hqu.lly.utils;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * UI控件工具类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/22 14:27
 */
@Slf4j
public class UIUtil {

    /**
     * 系统剪贴板
     */
    private static Clipboard clipboard = Clipboard.getSystemClipboard();

    /**
     * 应用程序主窗口
     */
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        if (getPrimaryStage() == null) {
            UIUtil.primaryStage = primaryStage;
        } else {
            log.warn("primaryStage can only be set once");
        }
    }

    /**
     * <p>
     * 获取有阴影的窗口{@link Scene}
     * </p>
     *
     * @param pane 要显示的面板节点
     * @param width 窗口宽度
     * @param height 窗口高度
     * @return {@link Scene} 有阴影的窗口
     * @date 2023-02-07 14:49:07 <br>
     */
    public static Scene getShadowScene(Parent pane, double width, double height) {
        BorderPane borderPane = new BorderPane(pane);
        pane.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK,5,0.1,0,0));
        borderPane.setPadding(new Insets(5, 5, 5, 5));

        // 以css的方式设置背景为透明，防止被其他css覆盖
        borderPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(borderPane, width, height, Color.TRANSPARENT);

        return scene;

    }
    public static void noShadowScene(Parent pane) {
        BorderPane borderPane = new BorderPane(pane);
        pane.setEffect(null);
    }

    /**
     * <p>
     * 获取toolTip
     * </p>
     *
     * @param content   tooltip内容
     * @param showDelay tooltip显示延时
     * @return {@link Tooltip}
     * @date 2022-09-22 14:31:56 <br>
     */
    public static Tooltip getTooltip(String content, long showDelay) {
        Tooltip tooltip = new Tooltip(content);
        tooltip.setShowDelay(new Duration(showDelay));
        return tooltip;
    }

    /**
     * <p>
     * 获取默认tooltip
     * </p>
     *
     * @param content tip内容
     * @return {@link Tooltip}
     * @date 2022-10-02 17:27:19 <br>
     */
    public static Tooltip getTooltip(String content) {
        return getTooltip(content, 300);
    }

    /**
     * <p>
     * 获取消息label
     * </p>
     *
     * @param msg      消息内容
     * @param fixWidth label宽度,用于指定在何地换行
     * @param softWrap 长消息是否换行标识
     * @return {@link Label} 消息label
     * @date 2022-09-24 09:17:06 <br>
     */
    public static Label getMsgLabel(String msg, double fixWidth, boolean softWrap) {
        Label msgLabel = new Label(msg);
        msgLabel.setPadding(new Insets(0, 0, 0, 0));
        double labelWidth = softWrap ? fixWidth : Region.USE_COMPUTED_SIZE;
        msgLabel.setPrefWidth(labelWidth);
        msgLabel.setWrapText(softWrap);
        return msgLabel;
    }

    /**
     * <p>
     * 返回修正后的msgLabel长度
     * </p>
     *
     * @param rawWidth msgLabel所在的父容器宽度
     * @return 修正后的msgLabel长度
     * @date 2022-10-02 19:13:08 <br>
     */
    public static double getFixMsgLabelWidth(double rawWidth) {
        return rawWidth - 30;
    }

    /**
     * <p>
     * 消息列表菜单
     * </p>
     *
     * @param msgList 要设置菜单的消息列表本身
     * @return {@link ContextMenu} 菜单
     * @date 2022-09-24 14:50:53 <br>
     */
    public static ContextMenu getMsgListMenu(ListView<Label> msgList) {
        ContextMenu menu = new ContextMenu();

        // 复制整条消息
        MenuItem copyItem = new MenuItem("copy");
        copyItem.setOnAction((ActionEvent e) -> {
            copyToClipboard(msgList.getSelectionModel().getSelectedItem().getText());
        });

        // 只复制消息内容
        MenuItem copyContentItem = new MenuItem("copy content");
        copyContentItem.setOnAction((ActionEvent e) -> {
            // 正则匹配消息头
            String pattern = "[\\s\\S]+?\\]\\s";
            String contentText = msgList.getSelectionModel().getSelectedItem().getText().replaceFirst(pattern, "");
            copyToClipboard(contentText);
        });

        menu.getItems().addAll(copyItem, copyContentItem);
        return menu;
    }

    /**
     * <p>
     * 复制文本到系统剪贴板
     * </p>
     *
     * @param text 要复制的文本
     * @date 2022-09-24 10:35:43 <br>
     */
    private static void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    /**
     * <p>
     * 改变消息label的状态(超长文本是否换行)
     * </p>
     *
     * @param msgLabel 消息label
     * @param fixWidth label宽度,用于指定在何地换行
     * @param softWrap 长消息是否换行标识
     * @date 2022-09-24 09:17:06 <br>
     */
    public static void changeMsgLabel(Label msgLabel, double fixWidth, boolean softWrap) {
        msgLabel.setPrefWidth(fixWidth);
        msgLabel.setWrapText(softWrap);
    }

}
