import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
        DocumentContent oldContent = contentFactory.create("Before Refactoring" + before.getText(), virtualFile);
        DocumentContent newContent = contentFactory.create("After Refactoring" + after.getText(), virtualFile);
        SimpleDiffRequest request = new SimpleDiffRequest(null, oldContent, newContent, "Before", "After");

        DiffRequestPanel diffPanel = DiffManager.getInstance().createRequestPanel(project, getDisposable(), null);
        diffPanel.putContextHints(DiffUserDataKeys.PLACE, "ExtractSignature");
        diffPanel.setRequest(request);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(diffPanel.getComponent(), BorderLayout.CENTER);
        panel.setBorder(IdeBorderFactory.createEmptyBorder(JBUI.insetsTop(5)));
        return panel;
    }
}