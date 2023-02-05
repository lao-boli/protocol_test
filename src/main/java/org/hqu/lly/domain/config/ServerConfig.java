package org.hqu.lly.domain.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用于保存服务面板的配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/29 13:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServerConfig extends TabConfig {

    /**
     * 本地服务端口号
     */
    private String port;

    /**
     * 反序列化标志,指定要反序列化的子类
     */
    static final String TYPE = "server";

    public ServerConfig() {
        setType(TYPE);
    }
}
