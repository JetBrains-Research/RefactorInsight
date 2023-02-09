package org.jetbrains.research.refactorinsight.ui;

import com.intellij.openapi.util.Key;
import org.jetbrains.research.refactorinsight.diff.MoreSidedDiffRequestGenerator;
import org.jetbrains.research.refactorinsight.diff.ThreeSidedRange;

import java.util.List;

public class Keys {
    public static final Key<List<ThreeSidedRange>> THREESIDED_RANGES =
            Key.create("refactoringMiner.List<ThreeSidedRange>");
    public static final Key<List<MoreSidedDiffRequestGenerator.MoreSidedRange>> MORESIDED_RANGES =
            Key.create("refactoringMiner.List<MoreSidedDiffRequestGenerator.Data>");
    public static final Key<Boolean> REFACTORING =
            Key.create("refactoringMiner.isRefactoringDiff");
    public static final Key<String> COMMIT_ID =
            Key.create("refactoringMiner.commitId");
    public static final Key<String> CHILD_COMMIT_ID =
            Key.create("refactoringMiner.childCommitId");
}
