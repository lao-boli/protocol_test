package org.hqu.lly.domain.config;

import org.hqu.lly.domain.bean.SendSettingConfig;

/**
 * <p>
 * 会话配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/6/28 20:31
 */
public class SessionConfig extends Config {

    String id;

    String type;

    String protocol;

    protected String tabName;

    /**
     * 消息框中输入的消息
     */
    protected String msgInput;

    SendSettingConfig sendSettingConfig;

    public SessionConfig() {
        this.sendSettingConfig = new SendSettingConfig();
    }

    public SessionConfig(SendSettingConfig sendSettingConfig) {
        this.sendSettingConfig = sendSettingConfig;
    }

}
