import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.comparison.ComparisonManagerImpl;
import com.intellij.diff.comparison.InnerFragmentsPolicy;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.text.LineOffsets;
import com.intellij.diff.tools.util.text.LineOffsetsUtil;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.diff.util.Range;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SampleDialogWrapper extends DialogWrapper {

    private final Project project;
    private PsiElement before;
    private PsiElement after;
    final private VirtualFile virtualFile;

    public SampleDialogWrapper(Project project, PsiElement before, PsiElement after) {
        super(true); // use current window as parent
        this.project = project;
        final File file = new File("/home/matei/IdeaProjects/template/src/main/java/calculators/FoodCalculator.java");
        virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        this.before = PsiManager.getInstance(project).findFile(virtualFile);
        this.after = PsiManager.getInstance(project).findFile(virtualFile);
        init();
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        //final PsiFile[] psiFile = FilenameIndex.getFilesByName(project, "/home/matei/IdeaProjects/template/src/main/java/calculators/FoodCalculator.java", GlobalSearchScope.allScope(project));


        DiffContentFactory contentFactory = DiffContentFactory.getInstance();

        DocumentContent oldContent = contentFactory.create(project,"Before Refactoring" + before.getText(), virtualFile);
        DocumentContent newContent = contentFactory.create(project, "After Refactoring" + after.getText(), virtualFile);
        SimpleDiffRequest request = new SimpleDiffRequest(null, oldContent, newContent, "Before", "After");
        ArrayList<Range> ranges = new ArrayList<>();
        ranges.add(new Range(4, 5, 6, 7));
        request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,getDiffComputer(ranges));

        DiffRequestPanel diffPanel = DiffManager.getInstance().createRequestPanel(project, getDisposable(), null);
        diffPanel.putContextHints(DiffUserDataKeys.PLACE, "ExtractSignature");
        diffPanel.setRequest(request);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(diffPanel.getComponent(), BorderLayout.CENTER);
        panel.setBorder(IdeBorderFactory.createEmptyBorder(JBUI.insetsTop(5)));
        return panel;
    }

    private static @NotNull DiffUserDataKeysEx.DiffComputer getDiffComputer(@NotNull Collection<? extends Range> ranges) {
        return (text1, text2, policy, innerChanges, indicator) -> {
            InnerFragmentsPolicy fragmentsPolicy = innerChanges ? InnerFragmentsPolicy.WORDS : InnerFragmentsPolicy.NONE;
            LineOffsets offsets1 = LineOffsetsUtil.create(text1);
            LineOffsets offsets2 = LineOffsetsUtil.create(text2);

            List<LineFragment> result = new ArrayList<>();
            ComparisonManagerImpl comparisonManager = ComparisonManagerImpl.getInstanceImpl();
            for (Range range : ranges) {
                result.addAll(comparisonManager.compareLinesInner(range, text1, text2, offsets1, offsets2, policy, fragmentsPolicy, indicator));
            }
            return result;
        };
    }
}