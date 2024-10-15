package org.hqu.lly.domain.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hqu.lly.enums.ConfigType;

/**
 * <p>
 * 服务类配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/6/28 20:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServerSessionConfig extends SessionConfig {

    /**
     * 本地服务端口号
     */
    protected String port;

    public ServerSessionConfig() {
        super();
        type = ConfigType.SERVER;
    }

    public String toString() {
        return this.id;
    }

}
