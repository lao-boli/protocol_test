package org.hqu.lly.domain.config;

import lombok.Data;
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
@Data
public abstract class TabConfig extends Config{

    protected String protocol;

    protected String tabName;

    protected String msgInput;

    protected SendSettingConfig sendSettingConfig;

}
