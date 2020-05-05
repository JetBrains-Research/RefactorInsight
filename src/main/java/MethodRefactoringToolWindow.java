import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import java.util.List;

public class MethodRefactoringToolWindow {

  private Project project;
  private ToolWindowManager toolWindowManager;
  private ToolWindow toolWindow;

  public MethodRefactoringToolWindow(Project project) {
    this.project = project;
    this.toolWindowManager = ToolWindowManager.getInstance(project);
    this.toolWindow = toolWindowManager
            .registerToolWindow("Method refactorings"
                    , true, ToolWindowAnchor.BOTTOM);
  }

  public void show(List<String> refactorings) {
    JBSplitter splitterPane = new JBSplitter(false, 0.6f);
    Content content;
    if (refactorings == null || refactorings.isEmpty()) {
        content = ContentFactory.SERVICE.getInstance()
                .createContent(splitterPane, "No refactorings" , false);
    } else {
        content = ContentFactory.SERVICE.getInstance()
                .createContent(splitterPane, refactorings.toString(), false);
    }
    toolWindow.getContentManager().addContent(content);
    toolWindow.show();
  }
}
