import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import java.awt.GridLayout;
import java.util.List;

public class MethodRefactoringPopup {

  private Project project;

  /**
   * Constructor for the tool window.
   * @param project the current project.
   */
  public MethodRefactoringPopup(Project project) {
    this.project = project;
  }

  /**
   * Show the list of refactorings in a tool window.
   * @param refactorings list of refactorings that should be
   *                     displayed.
   */
  public void show(List<String> refactorings, String methodName, DataContext datacontext) {
    JBPanel panel = new JBPanel(new GridLayout(0, 1));
    if (refactorings == null || refactorings.isEmpty()) {
      panel.add(new JBLabel("No refactorings for this method."));
    } else {
      for (String s : refactorings) {
        JBLabel lbl = new JBLabel(s);
        panel.add(lbl);
      }
    }
    JBPopup popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null).createPopup();
    popup.showInBestPositionFor(datacontext);
  }
}
