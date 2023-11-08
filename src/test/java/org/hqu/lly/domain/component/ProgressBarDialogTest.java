package org.hqu.lly.domain.component;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/8/13 20:48
 */
public class ProgressBarDialogTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProgressBarDialog progressBarDialog = new ProgressBarDialog(primaryStage,11,2);
        progressBarDialog.show();
    }



}
