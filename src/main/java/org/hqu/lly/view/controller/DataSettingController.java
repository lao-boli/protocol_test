package org.hqu.lly.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.DataItem;
import org.hqu.lly.domain.config.CustomDataConfig;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * <p>
 * 自定义数据格式设置面板控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-01-14 10:03
 */
@Slf4j
public class DataSettingController implements Initializable {

    @FXML
    private TitleBarController titleBarController;

    @FXML
    private VBox settingVBox;

    @Setter
    private CustomDataConfig dataConfig;

    @Setter
    private List<DataItem> dataItemList = new ArrayList<>();

    private List<Map<String, String>> boundList = new ArrayList<>();

    @FXML
    void saveSetting(MouseEvent event) {
        saveSetting();
    }

    void saveSetting() {
        boundList.clear();
        dataItemList.forEach(dataItem -> {
            boundList.add(dataItem.getData());
        });
        this.dataConfig.setBoundList(boundList);
    }

    /**
     * 注入功能函数，必须在工厂方法中调用
     */
    public void initFunction() {
        dataItemList = dataConfig.getDataItemList();
        for (DataItem dataItem : dataItemList) {
            settingVBox.getChildren().add(dataItem.getItemPane());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleBarController.initOnlyClose("数据值域设置");
        titleBarController.setOnBeforeClose(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                saveSetting();
                return true;
            }
        });
    }

}
