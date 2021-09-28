package org.jetbrains.research.refactorinsight.common.adapters;

public class Refactoring {
  private final String type;
  private final String name;
  private final String description;

  /**
   * Creates a wrapper for Refactoring instance provided by RefactoringMiner.
   *
   * @param refactoring from RefactoringMiner.
   */
  public Refactoring(org.refactoringminer.api.Refactoring refactoring) {
    this.type = refactoring.getRefactoringType().name();
    this.name = refactoring.getName();
    this.description = refactoring.toString();
  }

  /**
   * Creates a wrapper for Refactoring instance provided by kotlinRMiner.
   *
   * @param refactoring from kotlinRMiner.
   */
  public Refactoring(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring) {
    this.type = refactoring.getRefactoringType().name();
    this.name = refactoring.getName();
    this.description = refactoring.toString();
  }

  public String getRefactoringType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
