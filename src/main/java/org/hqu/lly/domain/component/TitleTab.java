package org.hqu.lly.domain.component;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.icon.CloseIcon;
import org.hqu.lly.utils.UIUtil;

import java.util.function.Supplier;

import static org.hqu.lly.utils.CommonUtil.getRealLength;

/**
 * <p>
 * 可编辑tab名称的tab页
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/26 13:23
 */
@Slf4j
public class TitleTab extends Tab {

    /**
     * 标签页所在的标签面板
     */
    protected TabPane tabPane;
    /**
     * tab页标题
     */
    private String tabTitle;
    /**
     * tab页标题输入框
     */
    @Getter
    private TextField tabTitleField;

    /**
     * 参见 {@link #getTabSkin(TextField)}
     */
    private Parent tabSkin;

    private HBox header;

    private CloseIcon closeIcon;

    private Supplier<Boolean> onCloseRequest;

    private Runnable onClosed;


    /**
     * <p>
     * {@link TitleTab}构造方法。
     * </p>
     *
     * @param tabTitle 标签页标题
     * @param tabPane  标签页所在的标签面板
     * @return {@link TitleTab}
     * @date 2023-02-26 13:36:11 <br>
     */
    public TitleTab(String tabTitle, TabPane tabPane) {
        super();
        this.tabTitle = tabTitle;
        this.tabPane = tabPane;
        initHeader();
    }

    /**
     * <p>
     * 初始化标题设置
     * </p>
     *
     * @date 2023-02-26 13:38:00 <br>
     */
    private void initTitle() {
        TextField title = new TextField(tabTitle);
        this.tabTitleField = title;
        // 加载css
        title.setEditable(false);

        title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 失去焦点事件
            title.setEditable(false);
            tabTitle = title.getText();
            this.setText(tabTitle);
        });

        // 传递拖拽相关事件给原生的tab处理类处理
        title.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            // 双击编辑标题
            if (event.getClickCount() == 2) {
                title.setEditable(true);
                title.setCursor(Cursor.TEXT);
            }
            // 单击且标题未处于编辑状态时传递事件
            if (event.getClickCount() == 1 && !title.isEditable()) {
                transmitEvent(title, event);
            }
        });
        title.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> transmitEvent(title, event));
        title.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> transmitEvent(title, event));
        setTooltip(title);

        this.setText(tabTitle);
    }

    /**
     * 初始化关闭图标 <br>
     *
     */
    private void initCloseIcon() {
        closeIcon = new CloseIcon();
        closeIcon.setIconHeight(10);
        closeIcon.setIconWidth(10);

        // I don't know why MOUSE_CLICKED doesn't work, so use MOUSE_RELEASED
        closeIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (onCloseRequest == null || onCloseRequest.get()) {
                if (onClosed != null){
                    onClosed.run();
                }
                tabPane.getTabs().remove(this);
            }
        });

    }

    private void initHeader() {
        initTitle();
        initCloseIcon();
        header = new HBox(tabTitleField, closeIcon);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);
        header.getStylesheets().add(ResLoc.TAB_TITLE.toExternalForm());
        this.setGraphic(header);
    }


    /**
     * 传递拖拽相关事件给原生的tab处理类处理
     *
     * @param title 标签页的 {@link #getGraphic()} 的返回值
     * @param event 传递的事件
     * @date 2023-03-22 21:30
     */
    private void transmitEvent(TextField title, MouseEvent event) {
        getTabSkin(title);
        Event.fireEvent(tabSkin, (Event) event.clone());
    }

    /**
     * 获取 {@link TabPaneSkin.TabHeaderSkin}, 原生的tab拖拽事件在此类中处理. <br>
     * 此方法应只在事件回调 EventHandler 或 EventFilter 中调用,否则可能造成空指针异常
     *
     * @param title 标签页的 {@link #getGraphic()} 的返回值
     * @date 2023-03-22 21:27
     */
    private void getTabSkin(TextField title) {
        if (tabSkin == null) {
            tabSkin = title.getParent().getParent().getParent();
        }
    }

    /**
     * <p>
     * 为标题 {@link TextField}设置 {@link Tooltip}
     * </p>
     *
     * @param title 标题{@link TextField}
     * @date 2023-02-26 13:47:27 <br>
     */
    private void setTooltip(TextField title) {
        // 生成提示框，出现时间设为极大，
        // 不让其自动出现，而是通过函数手动控制。
        Tooltip tooltip = UIUtil.getTooltip(tabTitle, Integer.MAX_VALUE);

        title.setOnMouseEntered(event -> {
            // 标题长度大于6个半角长度，就显示提示框
            if (getRealLength(tabTitle) > 6) {
                tooltip.setText(tabTitle);
                // 设置提示显示位置
                Bounds bounds = title.localToScreen(title.getBoundsInLocal());
                tooltip.show(title, bounds.getMinX(), bounds.getMinY() - 30);
            }
        });

        // 移动出标题框就隐藏提示框
        title.setOnMouseExited(event -> tooltip.hide());

        title.setTooltip(tooltip);
    }

    /**
     * 原生的 {@link Tab#setOnCloseRequest(EventHandler)}在回调中调用 {@link Stage#showAndWait()}后
     * ,再调用 {@link Event#consume()}会造成标签标题无响应,原因未知.
     * 故不使用原生的关闭图标和{@link Tab#setOnCloseRequest(EventHandler)}.
     * @param value 返回bool值的回调函数
     */
    public void setOnCloseRequest(Supplier<Boolean> value) {
        onCloseRequest = value;
    }

    /**
     * 使用 <br>
     * {@code tabPane.getTabs().remove(this); }<br>
     * 这种方法移除tab不会触发原生的 {@link Tab#onClosed}
     * 故需要采用自己的.
     * @param value 回调
     */
    public void setOnClosed(Runnable value) {
        onClosed = value;
    }

}
