package org.hqu.lly.factory;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.controller.DataSettingController;

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
@Slf4j
public class DataSettingPaneFactory {

    public static Stage create(CustomDataConfig dataConfig) {
        try {
            Stage dataSettingStage = new Stage();
            FXMLLoader loader = new FXMLLoader(ResLoc.DATA_SETTING_PANE);
            Parent contentPane = loader.load();

            DataSettingController controller = loader.getController();
            controller.setDataConfig(dataConfig);
            controller.initFunction();

            Scene scene = UIUtil.getShadowScene(contentPane, 400, 300);
            DarculaFX.applyDarculaStyle(scene);

            dataSettingStage.setScene(scene);
            dataSettingStage.initStyle(StageStyle.TRANSPARENT);
            DragUtil.setDrag(dataSettingStage, scene.getRoot());
            dataSettingStage.initModality(Modality.APPLICATION_MODAL);
            return dataSettingStage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
