package org.jetbrains.research.refactorinsight.ui.tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.Group;

public enum DisplayedGroup {
    METHOD,
    CLASS,
    VARIABLE,
    PARAMETER,
    PACKAGE,
    ANNOTATION;

    /**
     * Get displayed type of refactoring from internal representation.
     */
    @NotNull
    public static DisplayedGroup fromInternalGroup(@NotNull Group group) {
        return switch (group) {
            case METHOD -> METHOD;
            case ATTRIBUTE, VARIABLE, PARAMETER -> VARIABLE;
            case ABSTRACT, INTERFACE, CLASS -> CLASS;
            case PACKAGE -> PACKAGE;
            case ANNOTATION -> ANNOTATION;
        };
    }
}
