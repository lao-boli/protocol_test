package org.hqu.lly.domain.config;

import org.hqu.lly.utils.CommonUtil;

import java.lang.reflect.Field;

/**
 * <p>
 * 顶级配置父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/2 19:56
 */
public abstract class Config {

    public <T extends Config> T copyTo(T child) throws IllegalAccessException {
        Field[] fields = CommonUtil.getAllFields(this.getClass(),Config.class);
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(child, field.get(this));
        }
        return child;
    }

}
