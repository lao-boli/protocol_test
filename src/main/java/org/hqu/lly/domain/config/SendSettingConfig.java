package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(value = {"textMode","customMode","onModeChange"},ignoreUnknown = true)
public class SendSettingConfig extends Config {


    /**
     * 自定义数据配置类
     */
    private CustomDataConfig customDataConfig;

    /**
     * js脚本
     */
    private String jsScript;

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
    private static final String TEXT = "text";

    /**
     * 自定义数据模式
     */
    private static final String CUSTOM = "custom";

    /**
     * JS模式
     */
    private static final String JS = "js";

    /**
     * 模式切换时触发的回调函数
     */
    private TaskService onModeChange;

    public SendSettingConfig(CustomDataConfig customDataConfig, ScheduledSendConfig scheduledSendConfig) {
        this.customDataConfig = customDataConfig;
        this.scheduledSendConfig = scheduledSendConfig;
    }

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

    public void setJSMode() {
        mode = JS;
    }

    public boolean isTextMode() {
        return TEXT.equals(mode);
    }

    public boolean isCustomMode() {
        return CUSTOM.equals(mode);
    }

    public boolean isJSMode() {
        return JS.equals(mode);
    }


}
