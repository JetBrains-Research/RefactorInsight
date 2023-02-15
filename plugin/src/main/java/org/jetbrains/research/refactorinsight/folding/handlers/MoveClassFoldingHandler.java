package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.jetbrains.research.refactorinsight.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MoveClassFoldingHandler implements FoldingHandler{
    @Override
    public @NotNull List<FoldingDescriptor> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
        String path = isBefore ? info.getLeftPath() : info.getRightPath();
        if (!file.getVirtualFile().getPath().endsWith(path)) {
            return Collections.emptyList();
        }
        FoldingDescriptor descriptor = isBefore ? info.getFoldingDescriptorBefore() : info.getFoldingDescriptorAfter();
        if (!descriptor.hasHintText()) {
            String details = TextUtils.skipPackages(isBefore ? info.getDetailsAfter() : info.getDetailsBefore());
            String hintText = "Moved" + (isBefore ? " to " : " from ") + details;
            descriptor.addHintText(hintText);
        }
        return Collections.singletonList(descriptor);
    }

    @Override
    public @NotNull FoldingDescriptor uniteFolds(@NotNull List<FoldingDescriptor> folds) {
        FoldingDescriptor descriptor = folds.get(0);
        if (!descriptor.isHintTextUnited()) {
            String hintText = "Moved ";
            List<String> hints = folds.stream().map(FoldingDescriptor::getHintText).collect(Collectors.toList());
            hints = hints.stream().map(hint -> hint.substring("Moved ".length())).collect(Collectors.toList());

            if (hints.stream().allMatch(hint -> hint.startsWith("from"))) {
                hintText += "from ";
                hints = hints.stream().map(hint -> hint.substring("from ".length())).collect(Collectors.toList());
            } else if (hints.stream().allMatch(hint -> hint.startsWith("to"))) {
                hintText += "to ";
                hints = hints.stream().map(hint -> hint.substring("to ".length())).collect(Collectors.toList());
            } else {
                throw new AssertionError("Folds of different types");
            }

            hintText += String.join(", ", hints);
            String finalHintText = hintText;

            folds.forEach(eachDescriptor -> eachDescriptor.addUnitedHintText(finalHintText));
        }
        return descriptor;
    }
}
