package org.hqu.lly.domain.config;

import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.utils.ConfUtil;
import org.hqu.lly.view.controller.BaseController;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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



    public static final List<BaseController> controllers = new ArrayList<>();

    /**
     * 是否从文件加载的flag
     */
    private static Boolean isLoad = false;

    private static final Map<String,SessionConfig> sessionConfigs = new HashMap<>();

    public static void addSessionConfig(SessionConfig config){
        sessionConfigs.put(config.id, config);
    }

    public static void removeSessionConfig(String id){
        sessionConfigs.remove(id);
    }

    public static SessionConfig getById(String id) {
        return sessionConfigs.get(id);
    }

    public static SessionConfig createConfig(ConfigType type) {
        SessionConfig config = null;
        if (type == ConfigType.SERVER){
            config = new ServerSessionConfig();
            addSessionConfig(config);
        }
        if (type == ConfigType.CLIENT){
            config = new ServerSessionConfig();
            addSessionConfig(config);
        }
        return config;
    }

    public static void save(){
        controllers.forEach(BaseController::save);
        ConfUtil.saveConf(sessionConfigs);
    }

    public static void load() throws FileNotFoundException {
        ConfUtil.load();
        isLoad = true;
    }


}
