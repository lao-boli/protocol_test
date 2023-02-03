package org.hqu.lly.domain.config;

import lombok.Data;

/**
 * <p>
 * 用于保存服务面板的配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/29 13:15
 */
@Data
public class ServerConfig extends TabConfig {
    private String port;
}
