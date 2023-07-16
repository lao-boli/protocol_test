package org.hqu.lly.domain.config;

import lombok.Data;
import org.hqu.lly.utils.JSParser;

import java.util.List;

/**
 * <p>
 * 发送脚本配置
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/16 16:20
 */
@Data
public class JSCodeConfig {
    /**
     * js脚本
     */
    private String script;

    /**
     * 当前js引擎
     */
    private JSParser.EngineType engine;

    private List<StoringAreaConfig> storingCodes;

}
