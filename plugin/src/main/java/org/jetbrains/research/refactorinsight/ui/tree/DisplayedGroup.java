package org.jetbrains.research.refactorinsight.ui.tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.common.data.Group;

public enum DisplayedGroup {
  METHOD,
  CLASS,
  VARIABLE,
  PACKAGE;

  /**
   * Get displayed type of refactoring from internal representation.
   */
  @NotNull
  public static DisplayedGroup fromInternalGroup(@NotNull Group group) {
    switch (group) {
      case METHOD:
        return METHOD;
      case ATTRIBUTE:
      case VARIABLE:
        return VARIABLE;
      case ABSTRACT:
      case INTERFACE:
      case CLASS:
        return CLASS;
      case PACKAGE:
        return PACKAGE;
      default:
        throw new IllegalStateException("Unexpected value: " + group);
    }
  }
}
