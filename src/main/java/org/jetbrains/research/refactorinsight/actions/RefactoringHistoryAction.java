package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.usages.PsiElementUsageTarget;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import git4idea.repo.GitRepositoryManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.windows.HistoryType;
import org.jetbrains.research.refactorinsight.ui.windows.RefactoringHistoryToolbar;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.jetbrains.research.refactorinsight.utils.Utils;

/**
 * Represents the Show Refactoring History action.
 * If the currently opened project is a git repository, it retrieves
 * the refactoring history map that should be in the MiningService instance
 * of this project.
 * It checks if the selected PsiElement is instances of PsiClass, PsiMethod, or PsiField.
 * It computes the last object's signature and retrieves the data from the
 * refactoring history map.
 */
public class RefactoringHistoryAction extends AnAction {

  Map<String, Set<RefactoringInfo>> map;
  RefactoringHistoryToolbar refactoringHistoryToolbar;

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) {
      return;
    }
    if (GitRepositoryManager.getInstance(project).getRepositories().isEmpty()) {
      return;
    }
    map = e.getProject().getService(MiningService.class).getRefactoringHistory();

    DataContext dataContext = e.getDataContext();
    UsageTarget[] usageTarget = dataContext.getData(UsageView.USAGE_TARGETS_KEY);
    showHistory(project, dataContext, usageTarget);
  }

  /**
   * Checks if the selected object is instance of Class, Field or Method.
   *
   * @param project     the currently opened project
   * @param dataContext context in editor
   * @param usageTarget the target of the action call
   */
  private void showHistory(Project project, DataContext dataContext,
                           UsageTarget[] usageTarget) {
    if (usageTarget != null) {
      UsageTarget target = usageTarget[0];
      if (target instanceof PsiElementUsageTarget) {
        PsiElementUsageTarget elementUsageTarget = (PsiElementUsageTarget) target;
        PsiElement targetElement = elementUsageTarget.getElement();
        if (targetElement instanceof PsiMethod) {
          showHistoryMethod(project, dataContext, (PsiMethod) targetElement);
        } else if (targetElement instanceof PsiClass) {
          showHistoryClass(project, dataContext, (PsiClass) targetElement);
        } else if (targetElement instanceof PsiField) {
          showHistoryAttribute(project, dataContext, (PsiField) targetElement);
        }
      }
    }
  }

  private void showHistoryAttribute(Project project, DataContext dataContext,
                                    PsiField target) {
    String signature = StringUtils.getFieldSignature(target);
    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new HashSet<>()),
            target.getName(), dataContext, HistoryType.ATTRIBUTE, null, null);
  }

  private void showHistoryClass(Project project, DataContext dataContext, PsiClass psiClass) {
    String signature = psiClass.getQualifiedName();
    List<String> methods = Arrays.stream(psiClass.getMethods())
        .map(StringUtils::calculateSignature).collect(Collectors.toList());
    HashMap<String, Set<RefactoringInfo>> methodsHistory = new HashMap<>();
    methods.forEach(method -> methodsHistory.put(method, map.getOrDefault(method, new HashSet<>())));

    List<String> fields = Arrays.stream(psiClass.getFields())
        .map(StringUtils::getFieldSignature).collect(Collectors.toList());
    HashMap<String, Set<RefactoringInfo>> fieldsHistory = new HashMap<>();
    fields.forEach(field -> fieldsHistory.put(field, map.getOrDefault(field, new HashSet<>())));

    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new HashSet<>()),
            psiClass.getName(), dataContext, HistoryType.CLASS, methodsHistory, fieldsHistory);
  }

  private void showHistoryMethod(Project project, DataContext dataContext, PsiMethod method) {
    String signature = StringUtils.calculateSignature(method);
    getToolbarWindow(project)
        .showToolbar(map.getOrDefault(signature, new HashSet<>()),
            method.getName(), dataContext, HistoryType.METHOD, null, null);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(isRefactoringHistoryNotEmpty(e.getDataContext(), e.getProject()));
    super.update(e);
  }

  /**
   * Checks if refactoring history is not empty for the selected element.
   */
  private boolean isRefactoringHistoryNotEmpty(DataContext dataContext, Project project) {
    UsageTarget[] usageTarget = dataContext.getData(UsageView.USAGE_TARGETS_KEY);
    String key = "";
    UsageTarget target = usageTarget != null ? usageTarget[0] : null;
    if (target != null) {
      PsiElement element = ((PsiElementUsageTarget) target).getElement();
      if (element instanceof PsiMethod) {
        key = StringUtils.calculateSignature((PsiMethod) element);
      } else if (element instanceof PsiClass) {
        key = ((PsiClass) element).getQualifiedName();
      } else if (element instanceof PsiField) {
        key = StringUtils.getFieldSignature((PsiField) element);
      }
    }

    map = project.getService(MiningService.class).getRefactoringHistory();

    if (map != null) {
      return map.get(key) != null && !map.get(key).isEmpty();
    } else {
      return false;
    }
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

}
