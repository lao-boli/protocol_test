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

import java.util.List;
import java.util.stream.Collectors;

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
        contentPane.saveSetting();
        stage.close();
    }

    public void show() {
        stage.show();
    }

    /**
     *
     */
    public DataSettingPane(CustomDataConfig config, Stage parent) {

        // title bar
        titleBar = new TitleBar(this, "数据值域设置");
        pane.setTop(titleBar);

        // content pane
        contentPane = new ContentPane(config);
        pane.setCenter(contentPane);

        // css
        pane.getStylesheets().add(ResLoc.DATA_SETTING_PANE_CSS.toExternalForm());

        // scene and stage
        val scene = UIUtil.getShadowScene(pane, 450, 300);
        DragUtil.setDrag(stage, scene.getRoot());
        DarculaFX.applyDarculaStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        initOwner(parent);
    }


    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    class ContentPane extends VBox {

        // region base UI controls
        HBox btnGroup = new HBox();

        ScrollPane scrollPane = new ScrollPane();

        VBox settingVBox = new VBox();
        //endregion

        private List<DataItem> dataItemList;

        private CustomDataConfig config;

        public ContentPane(CustomDataConfig config) {
            this.config = config;
            init();
        }

        void saveSetting() {
            this.config.setBoundList(dataItemList.stream()
                                             .map(DataItem::getData)
                                             .collect(Collectors.toList()));
        }

        public void initDataItem() {
            dataItemList = config.getDataItemList();
            settingVBox.getChildren().addAll(dataItemList.stream()
                                                     .map(DataItem::getItemPane)
                                                     .collect(Collectors.toList()));
        }

        void init() {
            setupContent();
            setupBtnGroup();

            this.getStyleClass().add("content-pane");
            this.getChildren().addAll(scrollPane, btnGroup);

            initDataItem();
        }

        private void setupBtnGroup() {
            btnGroup.getStyleClass().add("btn-group");

            Button saveBtn = new Button("保存设置");
            saveBtn.setOnMouseClicked(e -> saveSetting());
            btnGroup.getChildren().add(saveBtn);
        }


        void setupContent() {
            settingVBox.getStyleClass().add("setting-VBox");
            settingVBox.prefWidthProperty().bind(scrollPane.widthProperty());

            VBox.setVgrow(scrollPane, Priority.ALWAYS);
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
