package org.hqu.lly.domain.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * config summary
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/8/19 17:16
 */
@Data
public class ConfigSummary {

    String version;

    Map<String, SessionConfig> sessionConfigs;

    public ConfigSummary() {
        version="0.3.0";
        sessionConfigs = new HashMap<>();
    }

}
