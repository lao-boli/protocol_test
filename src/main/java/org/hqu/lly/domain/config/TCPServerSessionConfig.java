package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TCPServerSessionConfig extends ServerSessionConfig {

    @JsonIgnore
    protected IdleStateHandler idleStateHandler;

    @JsonAlias("idleStateHandler")
    protected IdleStateProperties idleStateProp;

    public TCPServerSessionConfig() {
        super();
        type = ConfigType.TCP_SERVER;
        idleStateProp = new IdleStateProperties(0, 0, 0);
        idleStateHandler = new IdleStateHandler(idleStateProp.readerIdleTime,
                                                idleStateProp.writerIdleTime,
                                                idleStateProp.allIdleTime,
                                                TimeUnit.SECONDS);
    }

    static class IdleStateProperties {

        int readerIdleTime;
        int writerIdleTime;
        int allIdleTime;

        public IdleStateProperties() {
        }

        public IdleStateProperties(int readerIdleTime, int writerIdleTime, int allIdleTime) {
            this.readerIdleTime = readerIdleTime;
            this.writerIdleTime = writerIdleTime;
            this.allIdleTime = allIdleTime;
        }

    }

    public String toString() {
        return this.id;
    }

}
