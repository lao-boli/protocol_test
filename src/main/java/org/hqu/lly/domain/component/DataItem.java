package org.hqu.lly.domain.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 自定义数据格式的数值范围设置条目类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/16 10:10
 */
@Data
public class DataItem {

    private HBox itemPane;

    /**
     * 数据标签
     */
    private Label dataLabel;

    /**
     * 数据名称
     */
    private Label dataName;

    /**
     * 数据下界标签
     */
    private Label lowerBoundLabel;

    /**
     * 数据上界标签
     */
    private Label upperBoundLabel;

    /**
     * 数据下界文本框
     */
    private TextField lowerBoundText;

    /**
     * 数据上界文本框
     */
    private TextField upperBoundText;

    public DataItem() {
    }

    public DataItem(String dataItemName) {
        dataLabel = new Label("变动数值:");
        dataName = new Label(dataItemName);
        lowerBoundLabel = new Label("下界:");
        upperBoundLabel = new Label("上界:");
        lowerBoundText = new TextField();
        lowerBoundText.setPrefWidth(66.0);
        upperBoundText = new TextField();
        upperBoundText.setPrefWidth(66.0);

        itemPane = new HBox(dataLabel, dataName, lowerBoundLabel, lowerBoundText, upperBoundLabel, upperBoundText);
        itemPane.setSpacing(20.0);
        itemPane.setAlignment(Pos.CENTER);
    }

    public Map<String,String> getData(){
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name",dataName.getText());
        dataMap.put("lowerBound",lowerBoundText.getText());
        dataMap.put("upperBound",upperBoundText.getText());
        return dataMap;
    }


}
