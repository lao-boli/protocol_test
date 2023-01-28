package org.hqu.lly.domain.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.TaskService;

/**
 * <p>
 * 发送设置配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-01-27 08:33
 */
@Slf4j
@Data
public class SendSettingConfig {


    /**
     * 自定义数据配置类
     */
    private CustomDataConfig customDataConfig;

    /**
     * 定时发送配置类
     */
    private ScheduledSendConfig scheduledSendConfig;

    /**
     * 发送模式
     */
    private String mode;

    /**
     * 普通文本模式
     */
    private final String TEXT = "text";

    /**
     * 自定义数据模式
     */
    private final String CUSTOM = "custom";

    private TaskService onModeChange;

    public SendSettingConfig() {
        scheduledSendConfig = new ScheduledSendConfig();
        customDataConfig = new CustomDataConfig();
        mode = TEXT;
    }

    public void setTextMode() {
        mode = TEXT;
    }

    public void setCustomMode() {
        mode = CUSTOM;
    }

    public boolean isTextMode() {
        return TEXT.equals(mode);
    }

    public boolean isCustomMode() {
        return CUSTOM.equals(mode);
    }


}
