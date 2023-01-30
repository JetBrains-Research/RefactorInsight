package org.jetbrains.research.refactorinsight.adapters;

public abstract class Refactoring {
    private String type;
    private String name;
    private String description;

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
