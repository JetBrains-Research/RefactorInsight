import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SampleDialogWrapper extends DialogWrapper {

    private final Project project;

    public SampleDialogWrapper(Project project) {
        super(true); // use current window as parent
        this.project = project;
        init();
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
//        JPanel dialogPanel = new JPanel(new BorderLayout());
//
//        JLabel label = new JLabel("testing");
//        label.setPreferredSize(new Dimension(100, 100));
//        dialogPanel.add(label, BorderLayout.CENTER);
//
//        return dialogPanel;

        final VirtualFile file = PsiUtilCore.getVirtualFile(null);

        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DocumentContent oldContent = contentFactory.create("Before Refactoring", file);
        DocumentContent newContent = contentFactory.create("After Refactoring", file);
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