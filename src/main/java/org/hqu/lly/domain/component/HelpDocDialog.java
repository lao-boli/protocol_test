package org.hqu.lly.domain.component;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
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

    enum Direction {
        PREV,
        NEXT
    }

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
    }

    public void addText(String text){
        contentPane.addText(text);

    }

    @Override
    protected void initTitleBar() {
        initTitleBar("帮助文档");
    }

    class ContentPane extends VBox {

        // region base UI controls
        HBox header = new HBox();
        StackPane container = new StackPane();
        HBox btnGroup = new HBox();
        //endregion

        private int currentIndex = 2;

        private BooleanProperty isFirstPage = new SimpleBooleanProperty(true);

        private BooleanProperty isLastPage = new SimpleBooleanProperty(false);

        public ContentPane() {
            init();
        }


        void init() {
            setSpacing(5);
            setupHeader();
            setupContent();
            setupBtnGroup();

            this.getStyleClass().add("content-pane");
            this.getChildren().addAll(header, container, btnGroup);
            clipRegion(this);
        }


        private void setupHeader() {
            header.getStyleClass().add("header");

            Button nextBtn = new Button();
            nextBtn.setGraphic(new RightArrowIcon());
            nextBtn.setOnMouseClicked(event -> switchToNextPane());
            nextBtn.disableProperty().bind(isLastPage);
            Button prevBtn = new Button();
            prevBtn.setGraphic(new LeftArrowIcon());
            prevBtn.setOnMouseClicked(event -> switchToPreviousPane());
            prevBtn.disableProperty().bind(isFirstPage);

            header.getChildren().addAll(prevBtn, nextBtn);
        }

        private void setupBtnGroup() {
            btnGroup.getStyleClass().add("btn-group");
            Button closeBtn = new Button("关闭");
            closeBtn.setOnMouseClicked(event -> stage.close());
            btnGroup.getChildren().add(closeBtn);
        }

        void setupContent() {
            container.getStyleClass().add("container");
            VBox.setVgrow(container, Priority.ALWAYS);
        }

        void addText(String text) {
            container.getChildren().add(new DocNode(text));
            currentIndex = container.getChildren().size() - 1;
        }

        ObservableList<Node> getDocNodes() {
            return container.getChildren();
        }


        private void switchToPreviousPane() {
            int previousIndex = currentIndex == 0 ? getDocNodes().size() - 1 : currentIndex - 1;
            switchPane(previousIndex, Direction.PREV);
        }

        private void switchToNextPane() {
            int nextIndex = currentIndex == getDocNodes().size() - 1 ? 0 : currentIndex + 1;
            switchPane(nextIndex, Direction.NEXT);
        }

        private void switchPane(int newIndex, Direction direction) {
            Node curNode = getDocNodes().get(currentIndex);
            Node nextNode = getDocNodes().get(newIndex);

            curNode.setVisible(true);
            nextNode.setVisible(true);
            getDocNodes().forEach(n -> {
                if (!n.equals(curNode) && !n.equals(nextNode)){
                    n.setVisible(false);
                }
            });

            double targetX = getWidth();
            if (Direction.PREV.equals(direction)){
                targetX = -targetX;
            }

            nextNode.setTranslateX(targetX);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(curNode.translateXProperty(), 0)),
                    new KeyFrame(Duration.ZERO, new KeyValue(nextNode.translateXProperty(), targetX)),
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(curNode.translateXProperty(), -targetX, Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(nextNode.translateXProperty(), 0, Interpolator.EASE_BOTH))
            );

            timeline.setAutoReverse(false);
            timeline.setCycleCount(1);

            timeline.setOnFinished(event -> {
                currentIndex = newIndex;
                curNode.setVisible(false);
                nextNode.setVisible(true);

                if (currentIndex == 0 ){
                    isLastPage.setValue(true);
                    isFirstPage.setValue(false);
                }else if (currentIndex == getDocNodes().size() - 1){
                    isLastPage.setValue(false);
                    isFirstPage.setValue(true);
                }else {
                    isLastPage.setValue(false);
                    isFirstPage.setValue(false);
                }
            });

            timeline.play();
        }

        /**
         * 裁剪容器(避免显示的部分超出容器的范围)
         * @param region this
         */
        private void clipRegion(Region region) {
            Rectangle rectClip = new Rectangle();
            region.setClip(rectClip);
            Platform.runLater(() -> {
                rectClip.widthProperty().bind(region.widthProperty());
                rectClip.heightProperty().bind(region.heightProperty());
            });

        }

        void initAnim() {

        }

    }

    class DocNode extends StackPane {

        private final TextArea textArea;

        public DocNode(String text) {
            textArea = new TextArea(text);
            textArea.getStyleClass().add("doc-area");
            // textArea.setStyle("-fx-border-width: 0");
            textArea.setEditable(false);
            textArea.setWrapText(true);
            getChildren().add(textArea);
        }

    }


}
