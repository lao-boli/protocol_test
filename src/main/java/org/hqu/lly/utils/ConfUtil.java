package org.hqu.lly.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.config.ConfigStore;
import org.hqu.lly.domain.config.ConfigSummary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
public class ConfUtil {

    /**
     * JSON映射器
     */
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 配置文件读取和保存路径
     */
    private static final String path = System.getProperty("user.dir") + "\\config";
    /**
     * 配置文件名称
     */
    private static final String latest = "\\config.json";
    /**
     * 配置文件备份名称
     */
    private static final String backup = "\\config.bk.json";

    private static final Path dirPath = Path.of(path);
    private static final Path latestPath = Path.of(path + latest);
    private static final Path backupPath = Path.of(path + backup);

    static {
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * <p>
     * 保存配置文件
     * </p>
     *
     * @param o 配置文件对象
     * @date 2023-02-08 13:46:35 <br>
     */
    public static void saveConf(Object o) {
        try {

            boolean notExists = Files.notExists(dirPath);
            if (notExists) {
                Files.createDirectory(dirPath);
            }
            String s = mapper.writeValueAsString(o);
            log.debug(s);


            if (Files.exists(latestPath)) {
                Files.copy(latestPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.writeString(latestPath, s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载配置文件
     *
     * @return {@link ConfigStore#sessionConfigs} 全局配置文件
     * @throws FileNotFoundException 未读取到本地配置文件
     */
    public static ConfigSummary load() throws FileNotFoundException{
        try {
            FileInputStream fileInputStream = new FileInputStream(path + latest);
            ConfigSummary config = mapper.readValue(fileInputStream, ConfigSummary.class);
            return config;

        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
