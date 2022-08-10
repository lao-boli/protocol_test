package com.hqu.lly;
 
import com.github.mouse0w0.darculafx.DarculaFX;
import com.hqu.lly.view.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();

        URL resource = getClass().getClassLoader().getResource("main.fxml");
        fxmlLoader.setLocation(resource);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 400);
        DarculaFX.applyDarculaStyle(scene);

        MainController controller = fxmlLoader.getController();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Maven Test");
        primaryStage.show();
    }
}