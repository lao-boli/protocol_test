package org.hqu.lly.domain.component;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.handler.DragWindowHandler;

/**
 * <p>
 * 数据设置面板
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/5/30 21:12
 */
public class DataSettingPane {

    private final TitleBar titleBar;
    private final ContentPane contentPane;
    private Stage stage = new Stage();
    private BorderPane pane = new BorderPane();

    void close() {
        stage.close();
    }
   public void show() {
        stage.show();
    }

    /**
     *
     */
    public DataSettingPane(CustomDataConfig config) {
        titleBar = new TitleBar(this, "数据值域设置");
        pane.setTop(titleBar);
        contentPane = new ContentPane(this, config);
        pane.setCenter(contentPane);
        pane.getStylesheets().add(ResLoc.DATA_SETTING_PANE_CSS.toExternalForm());

        val scene = UIUtil.getShadowScene(pane, 300, 200);
        DragUtil.setDrag(stage, scene.getRoot());
        DarculaFX.applyDarculaStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
    }


    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    class ContentPane extends VBox {

        DataSettingPane rootPane;

        HBox btnGroup = new HBox();

        ScrollPane scrollPane = new ScrollPane();

        VBox settingVBox = new VBox();

        private CustomDataConfig config;

        public ContentPane(DataSettingPane rootPane) {
            init(rootPane);
        }

        public ContentPane(DataSettingPane rootPane, CustomDataConfig config) {
            init(rootPane);
            this.config = config;
        }

        void init(DataSettingPane rootPane) {
            this.rootPane = rootPane;
            this.getStyleClass().add("content-pane");
            setupContent();
            setupBtnGroup();
            this.getChildren().addAll(scrollPane, btnGroup);
        }

        private void setupBtnGroup() {
            btnGroup.getStyleClass().add("btn-group");
            btnGroup.getChildren().add(new Button("保存设置"));
        }


        void setupContent() {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            settingVBox.getStyleClass().add("setting-VBox");
            scrollPane.setContent(settingVBox);
        }

    }

    class TitleBar extends BorderPane {

        Label titleLabel;

        StackPane close;

        DataSettingPane dataSettingPane;

        public TitleBar(DataSettingPane dataSettingPane, String title) {
            this.dataSettingPane = dataSettingPane;
            this.getStyleClass().add("alert-title-bar");
            this.getStylesheets().add(ResLoc.ALERT_TITLE_BAR_CSS.toExternalForm());

            // todo perf ugly
            DragWindowHandler dragWindowHandler = new DragWindowHandler(dataSettingPane.stage);
            setOnMousePressed(dragWindowHandler);
            setOnMouseDragged(dragWindowHandler);

            setupClose();
            setupTitle(title);

        }

        private void setupTitle(String title) {
            titleLabel = new Label(title);
            titleLabel.getStyleClass().add("title");
            setLeft(titleLabel);
        }

        void setupClose() {
            close = new StackPane(new Region());
            close.getStyleClass().add("close");
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> dataSettingPane.close());
            setRight(close);

        }

    }



}
