package org.hqu.lly;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/17 17:26
 */
public class FormatterTest {

    private static final String formatSpecifier
            = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

    private static Pattern fsPattern = Pattern.compile(formatSpecifier);
    public static void main(String[] args) {
        String s = "%a%d%s%a";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(1.0);
        objects.add(1);
        objects.add("1");
        objects.add(1.0);
        Object[] objects1 = objects.toArray();
        String.format(s,objects1);
    }


}
