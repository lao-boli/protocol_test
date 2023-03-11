package org.hqu.lly.utils;


import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/19 16:14
 */
public class DataUtilTest {


    @Test
    public void randomFloat() {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", "1.5f");
        dataMap.put("upperBound", "5.5f");
        for (int i = 0; i < 10; i++) {
            Float aFloat = DataUtil.randomFloat(dataMap);
            System.out.println(aFloat);
            System.out.println(String.format("%10.4f", aFloat));
        }
    }

    private HashMap<String, String> getBoundMap(String lower, String upper) {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", lower);
        dataMap.put("upperBound", upper);
        return dataMap;
    }


    @Test
    public void randomDouble() {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", "1.5");
        dataMap.put("upperBound", "5.5");
        for (int i = 0; i < 10; i++) {
            Double aDouble = DataUtil.randomDouble(dataMap);
            System.out.println(aDouble);
            System.out.println(String.format("%10.4", aDouble));
        }
    }

    @Test
    public void randomInteger() {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", "0");
        dataMap.put("upperBound", "1");
        for (int i = 0; i < 10; i++) {
            Integer aInteger = DataUtil.randomInteger(dataMap);
//            if (aInteger == 1){
//                System.out.println(true);
//            }
            System.out.println(aInteger);
            System.out.println(String.format("%d", aInteger));
        }
    }

    @Test
    public void randomInteger16Radix() {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", "1");
        dataMap.put("upperBound", "FF");
        for (int i = 0; i < 10; i++) {
            Integer aInteger = DataUtil.randomInteger(dataMap,16);
            System.out.println(aInteger);
            System.out.println(String.format("%x", aInteger));
        }
    }

    @Test
    public void randomInteger8Radix() {
        HashMap<String, String> dataMap = new HashMap<>(3);
        dataMap.put("name", "a");
        dataMap.put("lowerBound", "1");
        dataMap.put("upperBound", "120");
        for (int i = 0; i < 10; i++) {
            Integer aInteger = DataUtil.randomInteger(dataMap,8);
            System.out.println(aInteger);
            System.out.println(String.format("%o", aInteger));
        }
    }
    @Test
    public void randomInteger8Radix2() {
        int i = Integer.parseInt("80", 8);
        System.out.println(i);
    }

}