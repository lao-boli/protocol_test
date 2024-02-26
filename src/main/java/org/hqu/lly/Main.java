package org.hqu.lly;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建第二个舞台
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Second Stage");

        // 设置第二个舞台的场景
        StackPane secondaryLayout = new StackPane();
        Scene secondaryScene = new Scene(secondaryLayout, 250, 150);
        secondaryLayout.getChildren().add(new Label("Loading Primary Stage..."));
        secondaryStage.setScene(secondaryScene);

        // 显示第二个舞台
        secondaryStage.show();

        // 模拟耗时的任务，比如加载资源
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 模拟加载过程
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(50);
                }
                return null;
            }
        };

        // 创建进度条并绑定到任务的进度
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());
        secondaryLayout.getChildren().add(progressBar);

        // 在任务完成后关闭第二个舞台并显示主舞台
        task.setOnSucceeded(event -> {
            primaryStage.show();
            secondaryStage.close();
        });

        // 启动任务
        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
