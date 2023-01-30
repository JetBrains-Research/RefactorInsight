package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.jetbrains.research.refactorinsight.utils.Utils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.jetbrains.research.kotlinrminer.common.RefactoringType.*;

public class MoveOperationFoldingHandler implements FoldingHandler {
    @NotNull
    @Override
    public List<FoldingDescriptor> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
        String path = isBefore ? info.getLeftPath() : info.getRightPath();
        if (!file.getVirtualFile().getPath().endsWith(path)) {
            return Collections.emptyList();
        }
        FoldingDescriptor descriptor = isBefore ? info.getFoldingDescriptorBefore() : info.getFoldingDescriptorAfter();
        if (!descriptor.hasHintText()) {
            String details = "";
            if (info.getRightPath().equals(info.getLeftPath())) {
                details = Utils.skipPackages(isBefore ? info.getDetailsAfter() : info.getDetailsBefore());
            }

            if (details.isEmpty()) {
                details = isBefore ? info.getRightPath() : info.getLeftPath();
                details = details.substring(details.lastIndexOf(File.separatorChar) + 1);
            }

            String hintText = specificOperation(info.getType())
                    + (isBefore ? " to " : " from ")
                    + details
                    + (info.isChanged() ? " with changes" : " without changes");
            descriptor.addHintText(hintText);
        }

        return Collections.singletonList(descriptor);
    }

    @NotNull
    @Override
    public FoldingDescriptor uniteFolds(@NotNull List<FoldingDescriptor> folds) {
        FoldingDescriptor descriptor = folds.get(0);
        if (!descriptor.isHintTextUnited()) {
            String hintText;
            List<String> hints = folds.stream().map(FoldingDescriptor::getHintText).collect(Collectors.toList());

            if (hints.stream().allMatch(hint -> hint.startsWith("Moved"))) {
                hintText = "Moved ";
                hints = hints.stream().map(hint -> hint.substring("Moved ".length())).collect(Collectors.toList());
            } else if (hints.stream().allMatch(hint -> hint.startsWith("Pulled up"))) {
                hintText = "Pulled up ";
                hints = hints.stream().map(hint -> hint.substring("Pulled up ".length())).collect(Collectors.toList());
            } else if (hints.stream().allMatch(hint -> hint.startsWith("Pushed down"))) {
                hintText = "Pushed down ";
                hints = hints.stream().map(hint -> hint.substring("Pushed down ".length())).collect(Collectors.toList());
            } else {
                throw new AssertionError("Folds of different types");
            }

            if (hints.stream().allMatch(hint -> hint.startsWith("from"))) {
                hintText += "from ";
                hints = hints.stream().map(hint -> hint.substring("from ".length())).collect(Collectors.toList());
            } else if (hints.stream().allMatch(hint -> hint.startsWith("to"))) {
                hintText += "to ";
                hints = hints.stream().map(hint -> hint.substring("to ".length())).collect(Collectors.toList());
            } else {
                throw new AssertionError("Folds of different types");
            }

            boolean isNotChanged = false;
            if (hints.stream().allMatch(hint -> hint.endsWith("without changes"))) {
                isNotChanged = true;
                hints = hints.stream()
                        .map(hint -> hint.substring(0, hint.length() - " without changes".length()))
                        .distinct()
                        .collect(Collectors.toList());
            } else {
                hints = hints.stream()
                        .map(hint -> hint.endsWith("without changes")
                                ? hint.substring(0, hint.length() - " without changes".length())
                                : hint.substring(0, hint.length() - " with changes".length()))
                        .distinct()
                        .collect(Collectors.toList());
            }

            if (hints.size() < 4) {
                hintText += String.join(", ", hints);
            } else {
                hintText += hints.size() + " places";
            }

            if (isNotChanged) {
                hintText += " without changes";
            }

            String finalHintText = hintText;
            folds.forEach(eachDescriptor -> eachDescriptor.addUnitedHintText(finalHintText));
        }
        return descriptor;
    }

    @NotNull
    @Contract(pure = true)
    private String specificOperation(@NotNull String type) {
        if (type.equals(MOVE_OPERATION.getDisplayName()) || type.equals(MOVE_AND_RENAME_OPERATION.getDisplayName())) {
            return "Moved";
        } else if (type.equals(PULL_UP_OPERATION.getDisplayName())) {
            return "Pulled up";
        } else if (type.equals(PUSH_DOWN_OPERATION.getDisplayName())) {
            return "Pushed down";
        }
        throw new AssertionError("Illegal refactoring type");
    }
}
