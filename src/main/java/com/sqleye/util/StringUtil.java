package com.sqleye.util;

import java.util.StringTokenizer;

/**
 * Created by chavezyuan on 16/9/13.
 */
public class StringUtil {

    public static String removeBreakingWhitespace(String original) {
        StringTokenizer whitespaceStripper = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        while (whitespaceStripper.hasMoreTokens()) {
            builder.append(whitespaceStripper.nextToken());
            builder.append(" ");
        }
        return builder.toString();
    }
}
