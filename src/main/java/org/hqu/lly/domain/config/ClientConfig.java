package org.hqu.lly.domain.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用于保存客户端面板的配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/29 13:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientConfig extends TabConfig {

    /**
     * 连接的服务端地址
     */
    private String serverAddr;

    /**
     * 反序列化标志,指定要反序列化的子类
     */
     static final String TYPE = "client";

    public ClientConfig() {
        setType(TYPE);
    }

}
