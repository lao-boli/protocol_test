package org.hqu.lly.domain.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hqu.lly.constant.ResLoc;

import java.util.function.Consumer;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/11/8 10:50
 */
public class ProgressBarDialog extends MyDialog<VBox> {

    private ProgressBar progressBar;

    private DoubleProperty total = new SimpleDoubleProperty(10);

    private DoubleProperty current = new SimpleDoubleProperty(0);

    private BooleanProperty autoClose = new SimpleBooleanProperty(true);

    private HBox progressBarBox;
    private Text progressText;

    private Text currentLoadingText;

    public Consumer<Text> updateCurrentLoadingText;

    public ProgressBarDialog() {
        init();
    }

    public ProgressBarDialog(Stage parent) {
        this(parent,10,0);
    }

    public ProgressBarDialog(Stage parent, double total, double current) {
        super(parent);
        this.total.setValue(total);
        this.current.setValue(current);
        init();
    }

    @Override
    protected Scene initScene() {
        return initScene(400, 100);
    }

    private void init() {
        currentLoadingText = new Text("");
        setupProgressBarBox();
        content = new VBox(progressBarBox, currentLoadingText);

        content.getStyleClass().add("content-pane");
        pane.getStyleClass().add("progress-bar-dialog");
        pane.getStylesheets().add(ResLoc.PROGRESS_BAR_DIALOG_CSS.toExternalForm());

        pane.setCenter(content);
        current.addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress(newValue.doubleValue() / total.getValue());
            progressText.setText(current.getValue().intValue() + "/" + total.getValue().intValue());
            if (updateCurrentLoadingText != null) {
                updateCurrentLoadingText.accept(currentLoadingText);
            }
            if (autoClose.get() && newValue.doubleValue() == total.doubleValue()) {
                close();
            }
        });
        // test();
    }

    public void setCurrentLoadingText(String text) {
        currentLoadingText.setText(text);
    }

    public void updateCurrentLoadingText(String text) {
        currentLoadingText.setText(text);
    }

    private void setupProgressBarBox() {
        progressBar = new ProgressBar(0);
        progressText = new Text(current.getValue().intValue() + "/" + total.getValue().intValue());
        progressBarBox = new HBox(progressBar, progressText);

        progressBarBox.getStyleClass().add("progress-bar-box");
    }

    public void setCurrent(double current) {
        this.current.setValue(current);
    }

    public void incCurrent() {
        current.setValue(current.getValue() + 1);
    }

    public void setTotal(double total) {
        this.total.setValue(total);
    }

    public void test() {
        Button addBtn = new Button("add");
        addBtn.setOnMouseClicked(event -> {
            current.setValue(current.getValue() + 1);

        });
        Button decBtn = new Button("dec");
        decBtn.setOnMouseClicked(event -> {
            current.setValue(current.getValue() - 1);

        });

        content.getChildren().addAll(addBtn, decBtn);

    }

}
