package org.jetbrains.research.refactorinsight.adapters;

/**
 * Represents the code change of the element made in the corresponding commit.
 */
public class CodeChange {
    private final String commitId;
    private final String changeType;
    private final String description;

    public CodeChange(String commitId, String changeType, String description) {
        this.commitId = commitId;
        this.changeType = changeType;
        this.description = description;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return changeType;
    }
}
