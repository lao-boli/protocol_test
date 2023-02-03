package org.hqu.lly.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.bean.SendSettingConfig;
import org.hqu.lly.domain.config.TopConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private static ObjectMapper mapper = new ObjectMapper();
    private static String path = System.getProperty("user.dir") + "\\config2.json";;
    {
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void saveConf(Object o) {
        try {
            String s = mapper.writeValueAsString(o);
            log.info(s);

            OutputStream os = new FileOutputStream(new File(path));
            Files.writeString(Path.of(path), s);
//            mapper.writeValue(new File(path),s);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TopConfig loadTopConf(){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            TopConfig config = mapper.readValue(fileInputStream, TopConfig.class);
            return config;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SendSettingConfig loadConf(){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            SendSettingConfig sendSettingConfig = mapper.readValue(fileInputStream, SendSettingConfig.class);
            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
