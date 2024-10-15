package org.hqu.lly.domain.component;

import javafx.application.Application;
import javafx.stage.Stage;
import org.hqu.lly.domain.config.SendSettingConfig;
import org.hqu.lly.page.SendSettingPane;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/8/13 20:48
 */
public class SendSettingPaneTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SendSettingPane sendSettingPane = new SendSettingPane(new SendSettingConfig());
        sendSettingPane.show();

    }



}
