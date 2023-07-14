package org.hqu.lly.domain.component;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.CustomDataConfig;

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
public class DataSettingPane extends MyDialog<DataSettingPane.ContentPane> {

    private final ContentPane contentPane;

    @Override
    public void close() {
        contentPane.saveSetting();
        stage.close();
    }

    /**
     *
     */
    public DataSettingPane(CustomDataConfig config, Stage parent) {
        super(parent);

        // content pane
        contentPane = new ContentPane(config);
        pane.setCenter(contentPane);

        // css
        pane.getStylesheets().add(ResLoc.DATA_SETTING_PANE_CSS.toExternalForm());

    }

    @Override
    protected void initTitleBar() {
        initTitleBar("数据值域设置");
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


}
