package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.usages.PsiElementUsageTarget;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import data.RefactoringInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import services.MiningService;
import ui.windows.HistoryType;
import ui.windows.RefactoringHistoryToolbar;
import utils.StringUtils;
import utils.Utils;

public class RefactoringHistoryAction extends AnAction {

  Map<String, ArrayList<RefactoringInfo>> map;
  RefactoringHistoryToolbar refactoringHistoryToolbar;

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
    showHistory(project, dataContext, usageTarget);
  }

  private void showHistory(Project project, DataContext dataContext,
                           UsageTarget[] usageTarget) {
    if (usageTarget != null) {
      UsageTarget target = usageTarget[0];
      if (target instanceof PsiElementUsageTarget) {
        if (((PsiElementUsageTarget) target).getElement() instanceof PsiMethod) {
          PsiMethod method = (PsiMethod) ((PsiElementUsageTarget) target).getElement();
          showHistoryMethod(project, dataContext, method);

        } else if (((PsiElementUsageTarget) target).getElement() instanceof PsiClass) {
          PsiClass psiClass = (PsiClass) ((PsiElementUsageTarget) target).getElement();

          showHistoryClass(project, dataContext, psiClass);
        } else if (((PsiElementUsageTarget) target).getElement() instanceof PsiField) {
          showHistoryAttribute(project, dataContext, (PsiElementUsageTarget) target);
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
  public RefactoringHistoryToolbar getToolbarWindow(Project project) {
    if (refactoringHistoryToolbar == null || Utils.manager == null) {
      refactoringHistoryToolbar = new RefactoringHistoryToolbar(project);
    }
    return refactoringHistoryToolbar;
  }

  private void showHistoryAttribute(Project project, DataContext dataContext,
                                    PsiElementUsageTarget target) {
    PsiField field = (PsiField) target.getElement();
    String signature = StringUtils.getFieldSignature(field);
    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new ArrayList<RefactoringInfo>()),
            field.getName(), dataContext, HistoryType.ATTRIBUTE, null, null);
  }

  private void showHistoryClass(Project project, DataContext dataContext, PsiClass psiClass) {
    String signature = psiClass.getQualifiedName();
    List<String> methods = Arrays.asList(psiClass.getMethods()).stream()
        .map(method -> StringUtils.calculateSignature(method)).collect(Collectors.toList());
    HashMap<String, ArrayList<RefactoringInfo>> methodsHistory = new HashMap<>();
    methods.forEach(method -> {
      methodsHistory.put(method, map.getOrDefault(method, new ArrayList<RefactoringInfo>()));
    });

    List<String> fields = Arrays.asList(psiClass.getFields()).stream()
        .map(field -> StringUtils.getFieldSignature(field)).collect(Collectors.toList());
    HashMap<String, ArrayList<RefactoringInfo>> fieldsHistory = new HashMap<>();
    fields.forEach(field -> {
      fieldsHistory.put(field, map.getOrDefault(field, new ArrayList<RefactoringInfo>()));
    });

    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new ArrayList<RefactoringInfo>()),
            psiClass.getName(), dataContext, HistoryType.CLASS, methodsHistory, fieldsHistory);
  }

  private void showHistoryMethod(Project project, DataContext dataContext, PsiMethod method) {
    String signature = StringUtils.calculateSignature(method);
    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new ArrayList<RefactoringInfo>()),
            method.getName(), dataContext, HistoryType.METHOD, null, null);
  }

}
