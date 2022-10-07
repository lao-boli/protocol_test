package org.hqu.lly.factory;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.view.controller.ScheduleSendController;

import java.io.IOException;

/**
 * <p>
 * 定时发送功能弹出窗口
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 19:33
 */
public class SendSettingPaneFactory {

    public static Stage create(ScheduledSendConfig sendConfig) {
        try {
            Stage sendSettingStage = new Stage();
            FXMLLoader loader = new FXMLLoader(SendSettingPaneFactory.class.getClassLoader().getResource(ResLocConsts.SEND_SETTING_PANE));
            Parent contentPane = loader.load();

            ScheduleSendController controller = loader.getController();
            controller.setSendConfig(sendConfig);

            Scene scene = new Scene(contentPane, 400, 300);
            DarculaFX.applyDarculaStyle(scene);

            sendSettingStage.setScene(scene);
            sendSettingStage.initStyle(StageStyle.TRANSPARENT);
            sendSettingStage.initModality(Modality.APPLICATION_MODAL);
            return sendSettingStage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
