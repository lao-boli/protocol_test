package org.hqu.lly;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hqu.lly.domain.component.HelpDocDialog;

public class PaneTransitionExample extends Application {
    private Pane pane1;
    private Pane pane2;
    private double paneWidth = 300;
    private boolean isPane1Visible = true;

    @Override
    public void start(Stage primaryStage) {
        Rectangle rectangle1 = new Rectangle(paneWidth, 200, Color.LIGHTBLUE);
        Rectangle rectangle2 = new Rectangle(paneWidth, 200, Color.LIGHTGREEN);

        pane1 = new Pane(rectangle1);
        pane2 = new Pane(rectangle2);

        Button button = new Button("Switch");
        button.setOnAction(event -> switchPanes());

        Pane root = new Pane();
        root.getChildren().addAll(pane1, pane2, button);

        Scene scene = new Scene(root, paneWidth, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        new HelpDocDialog(primaryStage).show();
    }

    private void switchPanes() {
        double targetTranslateX = isPane1Visible ? -paneWidth : paneWidth;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(pane1.translateXProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(pane2.translateXProperty(), 0)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(pane1.translateXProperty(), targetTranslateX, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(pane2.translateXProperty(), targetTranslateX, Interpolator.EASE_BOTH))
        );

        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> isPane1Visible = !isPane1Visible);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
