package org.jetbrains.research.refactorinsight.common.utils;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLType;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static final String ESC = "#";
    public static final String ESC_REGEX = "(?<!" + ESC + ")";
    public static final int ENTRY = 2;
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

    public static String calculateSignatureForJavaMethod(UMLOperation operation) {
        StringBuilder builder = new StringBuilder();
        List<String> parameterTypeList = new ArrayList<>();
        for (UMLType type : operation.getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        builder.append(operation.getClassName())
                .append(".")
                .append(calculateSignatureWithoutClassName(operation.getName(), parameterTypeList));
        return builder.toString();
    }

    public static String calculateSignatureForKotlinMethod(org.jetbrains.research.kotlinrminer.uml.UMLOperation operation) {
        StringBuilder builder = new StringBuilder();
        List<String> parameterTypeList = new ArrayList<>();
        for (org.jetbrains.research.kotlinrminer.uml.UMLType type : operation.getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        builder.append(operation.getClassName())
                .append(".")
                .append(calculateSignatureWithoutClassName(operation.getName(), parameterTypeList));
        return builder.toString();
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
}