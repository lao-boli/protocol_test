package org.hqu.lly.domain.config;

import lombok.Getter;
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
 * 配置类保存和加载仓库
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/1 19:46
 */
public class ConfigStore {

    public static final List<BaseController> controllers = new ArrayList<>();

    /**
     * 是否从文件加载的flag
     */
    public static Boolean isLoad = false;

    @Getter
    private static Map<String,SessionConfig> sessionConfigs = new HashMap<>();

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
            config = new ClientSessionConfig();
            addSessionConfig(config);
        }
        return config;
    }

    public static void save(){
        controllers.forEach(BaseController::save);
        ConfUtil.saveConf(sessionConfigs);
    }

    public static void load() throws FileNotFoundException {
        sessionConfigs = ConfUtil.load();
        isLoad = true;
    }


}
