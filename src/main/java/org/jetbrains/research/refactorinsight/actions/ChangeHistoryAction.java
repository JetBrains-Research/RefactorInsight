package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.usages.PsiElementUsageTarget;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;
import org.jetbrains.research.refactorinsight.services.ChangeHistoryService;
import org.jetbrains.research.refactorinsight.ui.windows.ChangeHistoryToolbar;
import org.jetbrains.research.refactorinsight.ui.windows.HistoryType;
import org.jetbrains.research.refactorinsight.utils.Utils;

import java.util.List;

import static org.jetbrains.research.refactorinsight.utils.Utils.getNumberOfMethodStartLine;

/**
 * Represents the `Show Change History` action.
 */
public class ChangeHistoryAction extends AnAction implements DumbAware {

    ChangeHistoryToolbar changeHistoryToolbar;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        if (GitRepositoryManager.getInstance(project).getRepositories().isEmpty()) {
            return;
        }

        DataContext dataContext = e.getDataContext();
        UsageTarget[] usageTarget = dataContext.getData(UsageView.USAGE_TARGETS_KEY);
        if (usageTarget != null) {
            UsageTarget target = usageTarget[0];
            if (target instanceof PsiElementUsageTarget) {
                PsiElementUsageTarget elementUsageTarget = (PsiElementUsageTarget) target;
                PsiElement targetElement = elementUsageTarget.getElement();
                if (targetElement instanceof PsiMethod) {
                    showChangeHistoryMethod(project, dataContext, (PsiMethod) targetElement);
                }
            }
        }
    }

    private void showChangeHistoryMethod(Project project, DataContext dataContext, PsiMethod method) {
        ChangeHistoryService changeHistoryService = new ChangeHistoryService();
        VirtualFile virtualFile = method.getContainingFile().getVirtualFile();
        VirtualFile contentRootForFile = ProjectFileIndex.getInstance(project).getContentRootForFile(virtualFile);
        if (contentRootForFile != null) {
            String projectPath = contentRootForFile.getPath();
            String filePath = virtualFile.getPath().replace(projectPath + "/", "");
            List<CodeChange> methodChangeHistory =
                    changeHistoryService.getHistoryForMethod(projectPath, filePath, method.getName(),
                            getNumberOfMethodStartLine(method.getContainingFile(), method.getTextOffset()));
            getToolbarWindow(project)
                    .showToolbar(method.getName(), HistoryType.METHOD, methodChangeHistory);
        }
    }

    public ChangeHistoryToolbar getToolbarWindow(Project project) {
        if (changeHistoryToolbar == null || Utils.manager == null) {
            changeHistoryToolbar = new ChangeHistoryToolbar(project);
        }
        return changeHistoryToolbar;
    }
}
