package org.hqu.lly.domain.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.utils.ConfUpdateUtil;
import org.hqu.lly.utils.ConfUtil;
import org.hqu.lly.view.controller.BaseController;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
@Slf4j
public class ConfigStore {

    public static final List<BaseController> controllers = new ArrayList<>();

    /**
     * 是否从文件加载的flag
     */
    public static Boolean isLoad = false;

    @Getter
    private static ConfigSummary configSummary = new ConfigSummary();
    @Getter
    private static Map<String,SessionConfig> sessionConfigs = configSummary.getSessionConfigs();

    public static void addSessionConfig(SessionConfig config){
        sessionConfigs.put(config.id, config);
        log.debug("add config {}",config);
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
        if (type == ConfigType.TCP_SERVER){
            config = new TCPServerSessionConfig();
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
        log.debug("save {}",sessionConfigs);
        ConfUtil.saveConf(configSummary);
    }

    public static void load() throws FileNotFoundException {
        configSummary = ConfUtil.load();
        if (configSummary != null){
            ConfUpdateUtil.updateConfig03to04(configSummary);
            sessionConfigs = configSummary.getSessionConfigs();
        }
        isLoad = true;
    }


}
