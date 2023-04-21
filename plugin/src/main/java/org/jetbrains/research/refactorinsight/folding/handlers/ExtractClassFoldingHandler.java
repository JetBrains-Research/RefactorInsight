package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.jetbrains.research.refactorinsight.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractClassFoldingHandler implements FoldingHandler {
    @Override
    public @NotNull List<FoldingDescriptor> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
        String path = isBefore ? info.getLeftPath() : info.getRightPath();
        if (isBefore || !file.getVirtualFile().getPath().endsWith(path)) {
            return Collections.emptyList();
        }
        FoldingDescriptor descriptor = info.getFoldingDescriptorAfter();
        if (!descriptor.hasHintText()) {
            String details = TextUtils.skipPackages(info.getDetailsBefore());
            String hintText = "Extracted from " + details;
            descriptor.addHintText(hintText);
        }
        return Collections.singletonList(descriptor);
    }

    @Override
    public @NotNull FoldingDescriptor uniteFolds(@NotNull List<FoldingDescriptor> folds) {
        FoldingDescriptor descriptor = folds.get(0);
        if (!descriptor.isHintTextUnited()) {
            String hintText = "Extracted from ";
            List<String> hints = folds.stream().map(FoldingDescriptor::getHintText).collect(Collectors.toList());
            hints = hints.stream().map(hint -> hint.substring("Extracted from ".length())).collect(Collectors.toList());

            if (hints.size() < 4) {
                hintText += String.join(", ", hints) + " classes";
            } else {
                hintText += hints.size() + " classes";
            }
            descriptor.addHintText(hintText);

            String finalHintText = hintText;
            folds.forEach(eachDescriptor -> eachDescriptor.addUnitedHintText(finalHintText));
        }

        return descriptor;
    }
}
