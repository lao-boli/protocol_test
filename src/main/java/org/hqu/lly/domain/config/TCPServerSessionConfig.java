package org.hqu.lly.domain.config;

import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hqu.lly.enums.ConfigType;

import java.util.concurrent.TimeUnit;

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
public class TCPServerSessionConfig extends  ServerSessionConfig{

    protected IdleStateHandler idleStateHandler;

    public TCPServerSessionConfig() {
        super();
        type = ConfigType.TCP_SERVER;
        idleStateHandler = new IdleStateHandler(0, 0, 0, TimeUnit.SECONDS);
    }

    public String toString() {
        return this.id;
    }

}
