package org.jetbrains.research.refactorinsight.utils;

import java.util.List;

public class StringUtils {
    public static final String ESC = "#";
    public static final String ESC_REGEX = "(?<!" + ESC + ")";
    public static final int MAP = 0;
    public static final int MAP_ENTRY = 1;
    public static final int ENTRY = 2;
    public static final int INFO = 3;
    public static final int LIST = 4;
    public static final int FRAG = 5;
    public static final int RANGE = 6;
    public static final String[] delimiters = {"_", "=", "`", "-", "!", ",", ";"};

    public static String delimiter(int option, boolean escaped) {
        return (escaped ? ESC_REGEX : "") + delimiters[option];
    }

    public static String delimiter(int option) {
        return delimiter(option, false);
    }

    /**
     * Calculates the method's signature without class name.
     *
     * @param operationName     operation's name.
     * @param parameterTypeList list of operation's parameters.
     * @return a String signature of the operation.
     */
    public static String calculateSignatureWithoutClassName(String operationName, List<String> parameterTypeList) {
        StringBuilder builder = new StringBuilder();
        builder.append(operationName)
                .append("(");

        parameterTypeList.forEach(x -> builder.append(x).append(", "));

        if (parameterTypeList.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");
        return builder.toString();
    }

    /**
     * Escapes the delimiter chars with '#'.
     *
     * @param s string to sanitize
     * @return escaped s
     */
    public static String sanitize(String s) {
        s = s.replaceAll(ESC, ESC + ESC);
        for (String d : delimiters) {
            s = s.replaceAll(d, ESC + d);
        }
        return s;
    }

    /**
     * Remove escape chars.
     *
     * @param s string to desanitize.
     * @return clean s
     */
    public static String deSanitize(String s) {
        for (String d : delimiters) {
            s = s.replaceAll(ESC + d, d);
        }
        return s.replaceAll(ESC + ESC, ESC);
    }

    public static String pathToClassName(String name) {
        return name.substring(name.indexOf('/') + 1, name.lastIndexOf('.'))
                .replace('/', '.');
    }

    /**
     * Method used for a presentable displaying of class change.
     *
     * @param nameBefore class name before.
     * @param nameAfter  class name after.
     * @return the index where the string are different.
     */
    public static int indexOfDifference(String nameBefore, String nameAfter) {
        int minLen = Math.min(nameBefore.length(), nameAfter.length());
        int last = 0;
        for (int i = 0; i != minLen; i++) {
            char chA = nameBefore.charAt(i);
            char chB = nameAfter.charAt(i);
            if (nameBefore.charAt(i) == '.' && nameAfter.charAt(i) == '.') {
                last = i + 1;
            }
            if (chA != chB) {
                return last;
            }
        }
        return last;
    }

    /**
     * Makes some refactoring names more intuitive for users.
     */
    public static String getPrettyName(String refactoringType) {
        return switch (refactoringType) {
            case "Replace Pipeline with Loop" -> "Replace Java Stream API with Loop";
            case "Replace Loop with Pipeline" -> "Replace Loop with Java Stream API";
            case "Encapsulate Attribute" -> "Make Field private and create getter";
            case "Collapse Hierarchy" -> "Merge Class with its Superclass";
            case "Localize Parameter" -> "Convert to Local Variable";
            case "Parameterize Variable" -> "Convert to Method Parameter";
            default -> refactoringType;
        };
    }
}
