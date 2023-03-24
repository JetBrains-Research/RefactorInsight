package org.jetbrains.research.refactorinsight.data;

public enum Group {
    METHOD,
    CLASS,
    ATTRIBUTE,
    VARIABLE,
    PARAMETER,
    INTERFACE,
    ABSTRACT,
    PACKAGE,
    ANNOTATION;

    public static final Group[] values = Group.values();
}