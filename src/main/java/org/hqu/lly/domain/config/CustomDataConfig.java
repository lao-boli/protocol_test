package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.DataItem;
import org.hqu.lly.utils.DataUtil;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *     自定义数据的配置类.
 * </p>
 * @author hqully
 * @version 1.0
 * @date 2023-01-14 10:31
 */
@Slf4j
@Data
public class CustomDataConfig {

    /**
     * 自定义数据格式
     */
    private String customDataPattern;

    /**
     * 自定义数据控件列表
     */
    @JsonIgnore
    private List<DataItem> dataItemList;

    /**
     * 自定义数据取值范围列表
     */
    private List<Map<String,String>> boundList;

    public CustomDataConfig() {

    }

    /**
     * <p>
     *     更新自定义数据面板的配置类,
     *     并根据{@link #customDataPattern}生成{@link #dataItemList}.
     * </p>
     * @param customDataPattern 自定义数据格式
     * @date 2023-01-14 10:31
     */
    public void updateConfig(String customDataPattern) {
        this.customDataPattern = customDataPattern;
        this.dataItemList = DataUtil.initDataItems(customDataPattern);
    }

    /**
     * <p>
     *     根据本地配置类中的{@link #boundList}生成{@link #dataItemList}.
     * </p>
     * @date 2023-02-05 10:31
     */
    public void loadLocalConfig() {
        this.dataItemList = DataUtil.initDataItems(boundList);
    }

}
