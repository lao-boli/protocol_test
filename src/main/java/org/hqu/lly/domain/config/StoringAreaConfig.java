package org.hqu.lly.domain.config;

import lombok.Data;

/**
 * <p>
 * 暂存区配置
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/16 18:36
 */
@Data
public class StoringAreaConfig {

    private String title;

    private String text;

    public StoringAreaConfig() {
    }
    public StoringAreaConfig(String title, String text) {
        this.title = title;
        this.text = text;
    }

}
