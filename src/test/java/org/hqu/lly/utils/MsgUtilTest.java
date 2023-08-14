package org.hqu.lly.utils;

import org.junit.jupiter.api.Test;

import static org.hqu.lly.enums.DataType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/4/2 19:37
 */
class MsgUtilTest {

    @Test
    void encodeBase64() {
        String test = MsgUtil.encodeBase64("test");
        MsgUtil.decodeBase64(test);
    }

    @Test
    void jsonFormat() {
        String jsonString = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        String s = MsgUtil.jsonFormat(jsonString);
        System.out.println(s);
    }
    @Test
    void encodeHex() {
        String test2 = "123测试test";
        String s = MsgUtil.encodeHex(test2);
        String s2 = MsgUtil.decodeHex(s);
        System.out.println(s);
        System.out.println(s2);
    }


    @Test
    void decodeHex() {
        String hexString = "48656c6c6f20576f726c64";
        String test = "74tt5737";
        String test2 = "我";
        String s = MsgUtil.decodeHex(test);
        String s2 = MsgUtil.decodeHex(test2);
        System.out.println(s);
        System.out.println(s2);

    }


    @Test
    void convertText() {
        String raw = "{\"key\":\"测试\",\"key2\":\"123\"}";
        String hex = MsgUtil.convertText(PLAIN_TEXT, HEX, raw);
        assertEquals("7B 22 6B 65 79 22 3A 22 E6 B5 8B E8 AF 95 22 2C 22 6B 65 79 32 22 3A 22 31 32 33 22 7D",hex);
        String base64 = MsgUtil.convertText(PLAIN_TEXT, BASE64, raw);
        assertEquals("eyJrZXkiOiLmtYvor5UiLCJrZXkyIjoiMTIzIn0=",base64);
        String json = MsgUtil.convertText(PLAIN_TEXT, JSON, raw);
        assertEquals("{\n  \"key\" : \"测试\",\n  \"key2\" : \"123\"\n}",json);

        String hexToPlain = MsgUtil.convertText(HEX, PLAIN_TEXT, hex);
        assertEquals("{\"key\":\"测试\",\"key2\":\"123\"}",hexToPlain);
        String hexToBase64 = MsgUtil.convertText(HEX, BASE64, hex);
        assertEquals("eyJrZXkiOiLmtYvor5UiLCJrZXkyIjoiMTIzIn0=",hexToBase64);
        String hexToJson = MsgUtil.convertText(HEX, JSON, hex);
        assertEquals("{\n  \"key\" : \"测试\",\n  \"key2\" : \"123\"\n}",hexToJson);

        String base64ToPlain = MsgUtil.convertText(BASE64, PLAIN_TEXT, base64);
        assertEquals("{\"key\":\"测试\",\"key2\":\"123\"}",base64ToPlain);
        String base64ToHex = MsgUtil.convertText(BASE64, HEX, base64);
        assertEquals("7B 22 6B 65 79 22 3A 22 E6 B5 8B E8 AF 95 22 2C 22 6B 65 79 32 22 3A 22 31 32 33 22 7D",base64ToHex);
        String base64ToJson = MsgUtil.convertText(BASE64, JSON, base64);
        assertEquals("{\n  \"key\" : \"测试\",\n  \"key2\" : \"123\"\n}",base64ToJson);

        String jsonToPlain = MsgUtil.convertText(JSON, PLAIN_TEXT, json);
        assertEquals("{\n  \"key\" : \"测试\",\n  \"key2\" : \"123\"\n}",jsonToPlain);
        String jsonToHex = MsgUtil.convertText(JSON, HEX, json);
        assertEquals("7B 0A 20 20 22 6B 65 79 22 20 3A 20 22 E6 B5 8B E8 AF 95 22 2C 0A 20 20 22 6B 65 79 32 22 20 3A 20 22 31 32 33 22 0A 7D",jsonToHex);
        String jsonToBase64 = MsgUtil.convertText(JSON, BASE64, json);
        assertEquals("ewogICJrZXkiIDogIua1i+ivlSIsCiAgImtleTIiIDogIjEyMyIKfQ==",jsonToBase64);

    }

}
