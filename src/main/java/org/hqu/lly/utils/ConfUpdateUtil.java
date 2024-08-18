package org.hqu.lly.utils;

import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.config.ConfigSummary;
import org.hqu.lly.domain.config.ServerSessionConfig;
import org.hqu.lly.domain.config.SessionConfig;
import org.hqu.lly.domain.config.TCPServerSessionConfig;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.enums.PaneType;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 * 保存和读取配置文件工具类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/28 19:33
 */
@Slf4j
public class ConfUpdateUtil {

    static String version = "0.4";

    public static boolean checkVersion(ConfigSummary config) {
        return config.getVersion() != null && config.getVersion().equals(version);
    }

    public static void updateConfig03to04(ConfigSummary config) {
        Collection<SessionConfig> configs = config.getSessionConfigs().values();
        if (config.getVersion().equals("0.3.0")) {
            var newConfigs = configs.stream().map(sessionConfig -> {
                if (sessionConfig instanceof ServerSessionConfig && sessionConfig.getPaneType().equals(PaneType.TCP_SERVER)) {
                    try {
                        TCPServerSessionConfig tcpServerSessionConfig = sessionConfig.copyTo(new TCPServerSessionConfig());
                        tcpServerSessionConfig.setType(ConfigType.TCP_SERVER);
                        return tcpServerSessionConfig;
                    } catch (IllegalAccessException e) {
                        log.error(e.toString());
                        return sessionConfig;
                    }
                } else {
                    return sessionConfig;
                }
            }).collect(Collectors.toMap(SessionConfig::getId, sessionConfig -> sessionConfig));
            config.setSessionConfigs(newConfigs);
            config.setVersion("0.4");

        }
    }

}
