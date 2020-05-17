package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import services.MiningService;

public class ClearRefactoringCacheAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project currentProject = e.getProject();
    MiningService miningService = currentProject.getService(MiningService.class);
    Map map = miningService.getState().map;
    map.clear();
  }
}