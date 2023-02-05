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
 * 自定义数据格式的数值范围设置条目类GUI组件
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/16 10:10
 */
@Data
public class DataItem {

    /**
     * 变动数值名称,例如 "%d"
     */
    private static final String NAME = "name";

    /**
     * 变动数值的上界
     */
    private static final String UPPER_BOUND = "upperBound";

    /**
     * 变动数值的上界
     */
    private static final String LOWER_BOUND = "lowerBound";

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

    /**
     * <p>
     *  根据变动数值名称生成 {@link DataItem}.
     * </p>
     * @param dataItemName 变动数值名称.例如 “%d”
     * @date 2023-02-06 14:56:35 <br>
     */
    public DataItem(String dataItemName) {
        init(dataItemName);
    }

    /**
     * <p>
     *     初始化数据条目.
     * </p>
     * @param dataItemName 数据条目中的要设置值域的变动数值名称.例如 “%d”
     * @date 2023-02-04 18:41:13 <br>
     * @author hqully <br>
     */
    private void init(String dataItemName) {
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

    /**
     * <p>
     *     用于从配置文件生成相应数据条目.
     * </p>
     * @param boundData 数据值域
     * @date 2023-02-04 18:39:57 <br>
     * @author hqully <br>
     */
    public DataItem(Map<String,String> boundData) {
        init(boundData.get("name"));
        lowerBoundText.setText(boundData.get("lowerBound"));
        upperBoundText.setText(boundData.get("upperBound"));
    }

    /**
     * <p>
     * 获取值域map. <br>
     *
     * </p>
     * @return 值域map,包含[ {@link #NAME},{@link #UPPER_BOUND},{@link #LOWER_BOUND}]
     * @date 2023-02-06 14:25:08 <br>
     */
    public Map<String,String> getData(){
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put(NAME,dataName.getText());
        dataMap.put(LOWER_BOUND,lowerBoundText.getText());
        dataMap.put(UPPER_BOUND,upperBoundText.getText());
        return dataMap;
    }



}
