package org.hqu.lly.domain.component;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.utils.UIUtil;

import java.util.function.Consumer;

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
public class TitleTab extends Tab {

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
     * 让 {@link TabPane}选中本{@link Tab}的回调
     */
    private Consumer<Tab> select;

    /**
     * <p>
     * {@link TitleTab}构造方法。
     * </p>
     *
     * @param tabTitle 标签页标题
     * @param select 让 {@link TabPane}选中本{@link Tab}的回调
     * @return {@link TitleTab}
     * @date 2023-02-26 13:36:11 <br>
     * @author hqully <br>
     */
    public TitleTab(String tabTitle, Consumer<Tab> select) {
        super();
        this.tabTitle = tabTitle;
        this.select = select;
        initTitle();
    }

    /**
     * <p>
     *     初始化标题设置
     * </p>
     * @date 2023-02-26 13:38:00 <br>
     */
    private void initTitle() {
        TextField title = new TextField(tabTitle);
        this.tabTitleField = title;
        // 加载css
        title.getStylesheets().add(getClass().getClassLoader().getResource(ResLocConsts.TAB_TITLE).toExternalForm());

        title.setMaxWidth(40);
        title.setEditable(false);
        title.setCursor(Cursor.DEFAULT);

        // 点击回调
        title.setOnMouseClicked(event -> {
            // 双击编辑标题
            if (event.getClickCount() == 2) {
                title.setEditable(true);
                title.setCursor(Cursor.TEXT);
            }
            // 单击切换到本标签页
            if (event.getClickCount() == 1) {
                select.accept(this);
            }
        });
        title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 失去焦点事件
            title.setEditable(false);
            title.setCursor(Cursor.DEFAULT);
            tabTitle = title.getText();
        });

        setTooltip(title);

        this.setGraphic(title);
    }

    /**
     * <p>
     *     为标题 {@link TextField}设置 {@link Tooltip}
     * </p>
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

        title.setOnMouseExited(event -> {
            // 移动出标题框就隐藏提示框
            tooltip.hide();
        });

        title.setTooltip(tooltip);
    }

}
