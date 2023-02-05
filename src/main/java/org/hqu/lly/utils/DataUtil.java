package org.hqu.lly.utils;

import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.DataItem;
import org.hqu.lly.exception.UnSetBoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 格式化自定义数据工具类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/17 10:41
 */
@Slf4j
public class DataUtil {

    /**
     * %[argument_index$][flags][width][.precision][t]conversion
     */
    private static final String formatSpecifier
            = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
    private static final String NAME = "name";
    private static final String LOWER_BOUND = "lowerBound";
    private static final String UPPER_BOUND = "upperBound";
    private static final Random rand = new Random();
    private static Pattern fsPattern = Pattern.compile(formatSpecifier);

    /**
     * <p>
     * 捕获自定义数据文本中的变量,并初始化{@link DataItem}列表
     * </p>
     *
     * @param dataText 自定义格式文本
     * @return  {@link DataItem}列表
     * @date 2023-01-18 15:36:19 <br>
     * @author hqully <br>
     */
    public static List<DataItem> initDataItems(String dataText) {
        ArrayList<DataItem> dataItems = new ArrayList<>();
        Matcher matcher = fsPattern.matcher(dataText);
        while (matcher.find()) {
            // [%%]为[%]的转义,故排除
            if (!"%%".equals(matcher.group())) {
                dataItems.add(new DataItem(matcher.group()));
            }
        }
        return dataItems;
    }

    /**
     * <p>
     *     从本地配置文件加载{@link DataItem}列表.
     * </p>
     * @param boundList 数据的值域Map列表,包含[name,lowerBound,upperBound].
     * @return  {@link DataItem}列表
     * @date 2023-02-04 15:56:08 <br>
     * @author hqully <br>
     */
    public static List<DataItem> initDataItems(List<Map<String, String>> boundList) {
        ArrayList<DataItem> dataItems = new ArrayList<>();

        if (boundList != null) {
            for (Map<String, String> bound : boundList) {
                dataItems.add(new DataItem(bound));
            }

        }
        return dataItems;
    }

    /**
     * <p>
     * 生成自定义格式的消息.
     * </p>
     * @param patternText 自定义格式文本
     * @param boundList 自定义格式的变量的值域列表
     * @return {@link String} 根据值域列表生成的随机数据消息
     * @date 2023-02-05 17:27:47 <br>
     */
    public static String createMsg(String patternText, List<Map<String,String>> boundList) {
        if (boundList == null) {
            throw new UnSetBoundException();
        }
        Object[] vars = new Object[boundList.size()];

        for (int i = 0; i < boundList.size(); i++) {
            Map<String, String> boundMap = boundList.get(i);
            String varName = boundMap.get(NAME);

            // Integer
            if (Conversion.isDecimalInteger(varName)){
                vars[i] = randomInteger(boundMap);
            }

            if (Conversion.isOctalInteger(varName)){
                vars[i] = randomInteger(boundMap,8);
            }

            if (Conversion.isHexadecimalInteger(varName)){
                vars[i] = randomInteger(boundMap,16);
            }

            // Float
            if (Conversion.isFloat(varName)){
                vars[i] = randomDouble(boundMap);
            }

        }
        String format = String.format(patternText, vars);

        return format;
    }

    public static Float randomFloat(Map<String, String> boundMap) {
        Float randomFloat = null;
        try {
            Float upperBound = Float.parseFloat(boundMap.get(UPPER_BOUND)) ;
            Float lowerBound = Float.parseFloat(boundMap.get(LOWER_BOUND));
            randomFloat = rand.nextFloat() * (upperBound - lowerBound) + lowerBound;
        } catch (NumberFormatException e) {
            throw new UnSetBoundException();
        }
        return randomFloat;
    }

    public static Double randomDouble(Map<String, String> boundMap) {
        Double randomDouble = null;
        try {
            Double upperBound = Double.parseDouble(boundMap.get(UPPER_BOUND)) ;
            Double lowerBound = Double.parseDouble(boundMap.get(LOWER_BOUND));
            randomDouble = rand.nextDouble() * (upperBound - lowerBound) + lowerBound;
        } catch (NumberFormatException e) {
            throw new UnSetBoundException();
        }
        return randomDouble;
    }

    public static Integer randomInteger(Map<String, String> boundMap,int radix) {
        int randomInteger = 0;
        try {
            Integer upperBound = Integer.parseInt(boundMap.get(UPPER_BOUND),radix);
            Integer lowerBound = Integer.parseInt(boundMap.get(LOWER_BOUND),radix);
            // [+1]保证能随机到上界,即随机区间为[lowerBound,upperBound]闭区间
            randomInteger = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
        } catch (NumberFormatException e) {
            throw new UnSetBoundException();
        }
        return randomInteger;
    }

    public static Integer randomInteger(Map<String, String> boundMap) {
        return randomInteger(boundMap,10);
    }

    private static class Conversion {
        // Byte, Short, Integer, Long, BigInteger
        // (and associated primitives due to autoboxing)
        static final String DECIMAL_INTEGER     = "%d";
        static final String OCTAL_INTEGER       = "%o";
        static final String HEXADECIMAL_INTEGER = "%x";
        static final String HEXADECIMAL_INTEGER_UPPER = "%X";

        // Float, Double, BigDecimal
        // (and associated primitives due to autoboxing)
        static final String SCIENTIFIC          = "%e";
        static final String SCIENTIFIC_UPPER    = "%E";
        static final String GENERAL             = "%g";
        static final String GENERAL_UPPER       = "%G";
        static final String DECIMAL_FLOAT       = "f";
        static final String HEXADECIMAL_FLOAT   = "%a";
        static final String HEXADECIMAL_FLOAT_UPPER = "%A";

        // Character, Byte, Short, Integer
        // (and associated primitives due to autoboxing)
        static final String CHARACTER           = "%c";
        static final String CHARACTER_UPPER     = "%C";

        // java.util.Date, java.util.Calendar, long
        static final String DATE_TIME           = "%t";
        static final String DATE_TIME_UPPER     = "%T";

        // if (arg.TYPE != boolean) return boolean
        // if (arg != null) return true; else return false;
        static final String BOOLEAN             = "%b";
        static final String BOOLEAN_UPPER       = "%B";
        // if (arg instanceof Formattable) arg.formatTo()
        // else arg.toString();
        static final String STRING              = "%s";
        static final String STRING_UPPER        = "%S";
        // arg.hashCode()
        static final String HASHCODE            = "%h";
        static final String HASHCODE_UPPER      = "%H";

        static final String LINE_SEPARATOR      = "%n";
        static final String PERCENT_SIGN        = "%%";

        static boolean isValid(String s) {
            return (isGeneral(s) || isInteger(s) || isFloat(s) || isText(s)
                    || "t".equals(s) || isCharacter(s));
        }

        // Returns true iff the Conversion is applicable to all objects.
        static boolean isGeneral(String s) {
            switch (s) {
                case BOOLEAN:
                case BOOLEAN_UPPER:
                case STRING:
                case STRING_UPPER:
                case HASHCODE:
                case HASHCODE_UPPER:
                    return true;
                default:
                    return false;
            }
        }

        // Returns true iff the Conversion is applicable to character.
        static boolean isCharacter(String s) {
            switch (s) {
                case CHARACTER:
                case CHARACTER_UPPER:
                    return true;
                default:
                    return false;
            }
        }

        // Returns true iff the Conversion is an integer type.
        static boolean isInteger(String s) {
            switch (s) {
                case DECIMAL_INTEGER:
                case OCTAL_INTEGER:
                case HEXADECIMAL_INTEGER:
                case HEXADECIMAL_INTEGER_UPPER:
                    return true;
                default:
                    return false;
            }
        }

        static boolean isDecimalInteger(String s) {
            return DECIMAL_INTEGER.equals(s);
        }

        static boolean isOctalInteger(String s) {
            return OCTAL_INTEGER.equals(s);
        }

        static boolean isHexadecimalInteger(String s) {
            return HEXADECIMAL_INTEGER.equals(s);
        }


        // Returns true iff the Conversion is a floating-point type.
        static boolean isFloat(String s) {
            if (s.endsWith(DECIMAL_FLOAT)){
               return true;
            }
            return false;
        }


        // Returns true iff the Conversion does not require an argument
        static boolean isText(String s) {
            switch (s) {
                case LINE_SEPARATOR:
                case PERCENT_SIGN:
                    return true;
                default:
                    return false;
            }
        }
    }


}
