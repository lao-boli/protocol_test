package org.hqu.lly;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hqu.lly.domain.component.HelpDocDialog;

import java.util.ArrayList;
import java.util.List;

public class DynamicPaneTransitionExample extends Application {

    private List<Pane> panes = new ArrayList<>();
    private int currentIndex = 0;
    private Pane root;

    @Override
    public void start(Stage primaryStage) {

        Rectangle rectangle1 = new Rectangle(300, 200, Color.LIGHTBLUE);
        Rectangle rectangle2 = new Rectangle(300, 200, Color.LIGHTGREEN);
        Rectangle rectangle3 = new Rectangle(300, 200, Color.LIGHTCORAL);

        panes.add(new Pane(rectangle1));
        panes.add(new Pane(rectangle2));
        panes.add(new Pane(rectangle3));

        Button prevButton = new Button("Prev");
        prevButton.setOnAction(event -> switchToPreviousPane());

        Button nextButton = new Button("Next");
        nextButton.setOnAction(event -> switchToNextPane());

        HBox buttonBox = new HBox(prevButton, nextButton);
        ;

        root = new VBox(buttonBox, new StackPane(panes.get(0), panes.get(1),panes.get(2)));
        // rectangle2.setVisible(false);
        // rectangle1.setVisible(false);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        new HelpDocDialog(primaryStage).show();
    }

    private void switchToPreviousPane() {
        int previousIndex = currentIndex == 0 ? panes.size() - 1 : currentIndex - 1;
        switchPane(previousIndex, -300);
    }

    private void switchToNextPane() {
        int nextIndex = currentIndex == panes.size() - 1 ? 0 : currentIndex + 1;
        switchPane(nextIndex, 300);
    }

    private void switchPane(int newIndex, double targetTranslateX) {
        Pane currentPane = panes.get(currentIndex);
        Pane newPane = panes.get(newIndex);
        // newPane.setVisible(false);
        currentPane.setVisible(true);
        newPane.setVisible(true);

        newPane.setTranslateX(targetTranslateX);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(currentPane.translateXProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(newPane.translateXProperty(), targetTranslateX)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(currentPane.translateXProperty(), -targetTranslateX, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(newPane.translateXProperty(), 0, Interpolator.EASE_BOTH))
        );

        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);

        timeline.setOnFinished(event -> {
            currentIndex = newIndex;
            currentPane.setVisible(false);
            newPane.setVisible(true);
        });

        timeline.play();
        // System.out.println(root.getChildren());
        // root.getChildren().remove(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
