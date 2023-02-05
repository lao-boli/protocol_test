package org.hqu.lly.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hqu.lly.domain.bean.SendSettingConfig;

/**
 * <p>
 * 客户端和服务端tab页配置父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/1 16:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientConfig.class,name = ClientConfig.TYPE),
        @JsonSubTypes.Type(value = ServerConfig.class,name = ServerConfig.TYPE)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TabConfig extends Config {

    /**
     * 标签页的标题
     */
    protected String tabName;

    /**
     * 消息框中输入的消息
     */
    protected String msgInput;

    /**
     * 发送设置配置
     */
    protected SendSettingConfig sendSettingConfig;

    /**
     * 反序列化标志,指定要反序列化的子类
     */
    private String type;

    public TabConfig() {
    }

}
