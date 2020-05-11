import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.usages.PsiElementUsageTarget;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class MethodHistory extends AnAction {

  ConcurrentHashMap<String, List<String>> map;
  MethodRefactoringPopup methodRefactoringPopup;

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
    map = project.getService(MethodService.class).getState().map;
    DataContext dataContext = e.getDataContext();
    UsageTarget[] usageTarget = dataContext.getData(UsageView.USAGE_TARGETS_KEY);
    if (usageTarget != null) {
      UsageTarget target = usageTarget[0];
      if (target instanceof PsiElementUsageTarget) {
        if (((PsiElementUsageTarget) target).getElement() instanceof PsiMethod) {
          PsiMethod method = (PsiMethod) ((PsiElementUsageTarget) target).getElement();
          String signature = method.getName();
          signature = method.getContainingClass().getQualifiedName() + "." + signature + "(";

          PsiParameterList parameterList = method.getParameterList();
          for (int i = 0; i < parameterList.getParametersCount(); i++) {
            if (i != parameterList.getParametersCount() - 1) {
              signature += parameterList.getParameter(i).getType().getPresentableText() + ",";
            } else {
              signature += parameterList.getParameter(i).getType().getPresentableText();
            }
          }

          signature += ")";
          System.out.println(signature);
          System.out.println(map.get(signature));

          getPopupWindow(project).show(map.get(signature), signature, dataContext);
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
   * Create or get a method refactorings tool window.
   *
   * @param project the current project.
   * @return a new method refactorings tool window.
   */
  public MethodRefactoringPopup getPopupWindow(Project project) {
    if (methodRefactoringPopup == null) {
      methodRefactoringPopup = new MethodRefactoringPopup(project);
    }
    return methodRefactoringPopup;

  }

}
