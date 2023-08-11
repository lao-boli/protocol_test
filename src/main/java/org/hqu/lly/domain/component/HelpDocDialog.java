package org.hqu.lly.domain.component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.icon.LeftArrowIcon;
import org.hqu.lly.icon.RightArrowIcon;

/**
 * <p>
 * 帮助文档弹框
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/5/30 21:12
 */
public class HelpDocDialog extends MyDialog<HelpDocDialog.ContentPane> {

    private final ContentPane contentPane;

    @Override
    public void close() {
        stage.close();
    }

    /**
     *
     */
    public HelpDocDialog(Stage parent) {
        super(parent);

        // content pane
        contentPane = new ContentPane();
        pane.setCenter(contentPane);

        // css
        pane.getStylesheets().add(ResLoc.HELP_DOC_DIALOG_CSS.toExternalForm());
        contentPane.addText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    }

    @Override
    protected void initTitleBar() {
        initTitleBar("帮助文档");
    }

    class ContentPane extends VBox {

        // region base UI controls
        HBox header = new HBox();
        HBox container = new HBox();
        HBox btnGroup = new HBox();
        //endregion

        public ContentPane() {
            init();
        }


        void init() {
            setupHeader();
            setupContent();
            setupBtnGroup();

            this.getStyleClass().add("content-pane");
            this.getChildren().addAll(header,container, btnGroup);
        }


        private void setupHeader() {
            header.getStyleClass().add("header");

            Button nextBtn = new Button();
            nextBtn.setGraphic(new RightArrowIcon());
            Button prevBtn = new Button();
            prevBtn.setGraphic(new LeftArrowIcon());

            header.getChildren().addAll(prevBtn, nextBtn);
        }
        private void setupBtnGroup() {
            btnGroup.getStyleClass().add("btn-group");
            Button closeBtn = new Button("关闭");
            btnGroup.getChildren().add(closeBtn);
        }

        void setupContent() {
            container.getStyleClass().add("container");
            VBox.setVgrow(container,Priority.ALWAYS);
        }

        void addText(String text) {
            Node c = createContent(text);
            container.getChildren().add(c);
        }
        Node createContent(String text) {
            TextArea textArea = new TextArea(text);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            StackPane stackPane = new StackPane(textArea);
            HBox.setHgrow(stackPane,Priority.ALWAYS);
            return stackPane;
        }

    }


}
