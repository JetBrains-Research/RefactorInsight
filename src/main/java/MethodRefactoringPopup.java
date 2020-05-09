import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BorderFactory;
import org.refactoringminer.api.RefactoringType;


public class MethodRefactoringPopup {

  private Project project;

  /**
   * Constructor for the tool window.
   *
   * @param project the current project.
   */
  public MethodRefactoringPopup(Project project) {
    this.project = project;
  }

  /**
   * Show the list of refactorings in a tool window.
   *
   * @param refactorings list of refactorings that should be
   *                     displayed.
   */
  public void show(List<String> refactorings, String methodName,
                   DataContext datacontext, AnActionEvent event) {

    JBPanel panel;
    Font font = new Font(".SFNS-Regular", Font.PLAIN, 13);

    if (refactorings == null || refactorings.isEmpty()) {
      panel = new JBPanel(new GridLayout(0, 1));
      panel.add(new JBLabel("No refactorings for this method."));
    } else {
      panel = new JBPanel(new GridLayout(refactorings.size(), 1));

      for (String str : refactorings) {
        Gson gson = new Gson();
        MethodRefactoring s = gson.fromJson(str, MethodRefactoring.class);
        String string = s.getData().getType().getDisplayName();
        if (s.getData().getType() == RefactoringType.RENAME_METHOD
            || s.getData().getType() == RefactoringType.MOVE_AND_RENAME_OPERATION) {
          string += "    " + s.getData().getMethodBefore()
              + " -> " + s.getData().getMethodAfter();
        }


        JBLabel label = new JBLabel(string);
        label.setFont(font);
        label.addMouseListener(new MouseListener() {
          @Override
          public void mouseClicked(MouseEvent e) {
            label.setFontColor(UIUtil.FontColor.BRIGHTER);
            VcsLogFilterCollection filters = VcsLogFilterObject.collection();
            VcsLogManager.LogWindowKind kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
            kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
            VcsProjectLog.getInstance(project).openLogTab(filters, kind)
                .getVcsLog().jumpToReference(s.getCommmitId());
          }

          @Override
          public void mousePressed(MouseEvent e) {

          }

          @Override
          public void mouseReleased(MouseEvent e) {

          }

          @Override
          public void mouseEntered(MouseEvent e) {

          }

          @Override
          public void mouseExited(MouseEvent e) {

          }


        });
        panel.add(label);
      }
      panel.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Refactoring history"));
    }


    JBPopup popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(panel, null).createPopup();
    popup.showInBestPositionFor(datacontext);
  }
}
