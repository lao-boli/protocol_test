package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.enums.PaneType;

import java.util.UUID;

/**
 * <p>
 * 会话配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/6/28 20:31
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientSessionConfig.class,name = "CLIENT"),
        @JsonSubTypes.Type(value = ServerSessionConfig.class,name = "SERVER")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionConfig extends Config {

    String id;

    ConfigType type;

    String protocol;

    PaneType paneType;

    protected String tabName;

    /**
     * 消息框中输入的消息
     */
    protected String msgInput;

    SendSettingConfig sendSettingConfig;

    public SessionConfig() {
        this.sendSettingConfig = new SendSettingConfig();
        this.id = UUID.randomUUID().toString();
    }

    public SessionConfig(SendSettingConfig sendSettingConfig) {
        this.sendSettingConfig = sendSettingConfig;
    }

}
