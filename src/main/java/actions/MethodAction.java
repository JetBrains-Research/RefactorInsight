package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.usages.PsiElementUsageTarget;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import data.RefactoringInfo;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import services.MiningService;
import ui.MethodRefactoringToolbar;
import utils.Utils;

public class MethodAction extends AnAction {

  Map<String, List<RefactoringInfo>> map;
  MethodRefactoringToolbar methodRefactoringToolbar;

  /**
   * Implement this method to provide your action handler.
   *
   * @param e Carries information on the invocation place
   */
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) {
      return;
    }
    map = e.getProject().getService(MiningService.class).getMethodHistory();
    DataContext dataContext = e.getDataContext();
    UsageTarget[] usageTarget = dataContext.getData(UsageView.USAGE_TARGETS_KEY);
    showHistoryForMethod(project, dataContext, usageTarget);
  }

  private void showHistoryForMethod(Project project, DataContext dataContext,
                                    UsageTarget[] usageTarget) {
    if (usageTarget != null) {
      UsageTarget target = usageTarget[0];
      if (target instanceof PsiElementUsageTarget) {
        if (((PsiElementUsageTarget) target).getElement() instanceof PsiMethod) {
          PsiMethod method = (PsiMethod) ((PsiElementUsageTarget) target).getElement();
          String signature = Utils.calculateSignature(method);
          getToolbarWindow(project).showToolbar(map.get(signature),
              method.getName(), dataContext);
        }
      }
    }
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    super.update(e);
  }

  /**
   * Create or get a method refactorings toolbar window.
   *
   * @param project the current project.
   * @return a new method refactorings toolbar window.
   */
  public MethodRefactoringToolbar getToolbarWindow(Project project) {
    if (methodRefactoringToolbar == null) {
      methodRefactoringToolbar = new MethodRefactoringToolbar(project);
    }
    return methodRefactoringToolbar;
  }

}
