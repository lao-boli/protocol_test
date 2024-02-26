package org.hqu.lly;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ServiceTest extends Application {

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

        // 创建Service来加载FXML文件
        FXMLLoadingService fxmlLoadingService = new FXMLLoadingService();

        // 创建进度条并绑定到Service的进度
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(fxmlLoadingService.progressProperty());
        secondaryLayout.getChildren().add(progressBar);

        // 在Service完成后关闭第二个舞台并显示主舞台
        fxmlLoadingService.setOnSucceeded(event -> {
            secondaryStage.close();
            primaryStage.show();
        });

        // 启动Service
        fxmlLoadingService.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 自定义Service来加载FXML文件
    private static class FXMLLoadingService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // 模拟加载过程
                    for (int i = 0; i <= 100; i++) {
                        System.out.println(Thread.currentThread().getName());
                        updateProgress(i, 100);
                        Thread.sleep(50);
                    }
                    return null;
                }
            };
        }
    }
}

