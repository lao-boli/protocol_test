package org.hqu.lly.domain.config;

import org.hqu.lly.utils.ConfUtil;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/1 19:46
 */
public class NewTopConfig {

    /**
     * 是否从文件加载的flag
     */
    private static Boolean isLoad = false;

    private static Map<String,SessionConfig> sessionConfigs;

    public static void addSessionConfig(SessionConfig config){
        sessionConfigs.put(config.id, config);
    }

    public static void removeSessionConfig(String id){
        sessionConfigs.remove(id);
    }

    public static SessionConfig getById(String id) {
        return sessionConfigs.get(id);
    }

    public static void save(){
        ConfUtil.saveConf(sessionConfigs);
    }

    public static void load() throws FileNotFoundException {
        ConfUtil.load();
        isLoad = true;
    }


}
