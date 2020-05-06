import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import java.util.List;

public class MethodRefactoringToolWindow {

  private Project project;
  private ToolWindowManager toolWindowManager;
  private ToolWindow toolWindow;

  /**
   * Constructor for the tool window.
   * @param project the current project.
   */
  public MethodRefactoringToolWindow(Project project) {
    this.project = project;
    this.toolWindowManager = ToolWindowManager.getInstance(project);
    this.toolWindow = toolWindowManager
            .registerToolWindow("Method refactorings",
                    true, ToolWindowAnchor.BOTTOM);
  }

  /**
   * Show the list of refactorings in a tool window.
   * @param refactorings list of refactorings that should be
   *                     displayed.
   */
  public void show(List<String> refactorings, String methodName) {
    JBSplitter splitterPane = new JBSplitter(false, 0.6f);
    Content content;
    if (refactorings == null || refactorings.isEmpty()) {
      content = ContentFactory.SERVICE.getInstance()
              .createContent(splitterPane, "No refactorings", false);
    } else {
      JBLabel label = new JBLabel(refactorings.toString());
      splitterPane.setFirstComponent(label);
      content = ContentFactory.SERVICE.getInstance()
              .createContent(splitterPane, methodName, false);
    }
    toolWindow.getContentManager().addContent(content);
    toolWindow.show();
  }
}
