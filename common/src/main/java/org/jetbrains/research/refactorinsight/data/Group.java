package org.jetbrains.research.refactorinsight.data;

public enum Group {
    METHOD,
    CLASS,
    ATTRIBUTE,
    VARIABLE,
    INTERFACE,
    ABSTRACT,
    PACKAGE;

    public static final Group[] values = Group.values();
}