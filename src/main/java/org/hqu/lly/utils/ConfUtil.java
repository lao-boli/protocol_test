package org.hqu.lly.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.config.SessionConfig;
import org.hqu.lly.domain.config.TopConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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
    private static final String name = "\\config.json";
    private static final String name2 = "\\config2.json";
    ;

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
            boolean notExists = Files.notExists(Path.of(path));
            if (notExists) {
                Files.createDirectory(Path.of(path));
            }
            String s = mapper.writeValueAsString(o);
            log.debug(s);
            Files.writeString(Path.of(path + name2), s);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * 加载配置文件
     * </p>
     *
     * @return {@link TopConfig} 全局配置文件
     * @throws FileNotFoundException 未读取到本地配置文件
     * @date 2023-02-08 13:47:11 <br>
     */
    public static TopConfig loadTopConf() throws FileNotFoundException {
        try {
            FileInputStream fileInputStream = new FileInputStream(path + name);
            TopConfig config = mapper.readValue(fileInputStream, TopConfig.class);
            return config;

        } catch (FileNotFoundException e) {
            throw e;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, SessionConfig> load() throws FileNotFoundException {
        try {
            FileInputStream fileInputStream = new FileInputStream(path + name2);
            HashMap config = mapper.readValue(fileInputStream, HashMap.class);
            return config;

        } catch (FileNotFoundException e) {
            throw e;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
