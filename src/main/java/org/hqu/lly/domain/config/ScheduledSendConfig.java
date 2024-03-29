package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.factory.SendTaskFactory;

/**
 * <p>
 * 定时发送配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/28 19:37
 */
@Slf4j
@Data
public class ScheduledSendConfig {

    /**
     * 总共发送次数
     */
    private Integer sendTimes;
    /**
     * 发送间隔,单位为ms.
     */
    private Integer interval;
    /**
     * 发送类型.
     */
    private Integer sendType;

    /**
     * 消息发送任务工厂
     */
    @JsonIgnore
    private SendTaskFactory taskFactory;


    public ScheduledSendConfig() {
        defaultInit();
    }

    private void defaultInit() {
        // 1000ms
        interval = 1000;
        // manual stop type
        sendType = 1;

    }

}
