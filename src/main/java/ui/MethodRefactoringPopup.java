package ui;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import data.RefactoringInfo;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;


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
  public void show(List<RefactoringInfo> refactorings, String methodName, DataContext datacontext) {

    JBPanel panel;
    if (refactorings == null || refactorings.isEmpty()) {
      panel = new JBPanel(new GridLayout(0, 1));
      panel.add(new JBLabel("No refactorings for this method."));
    } else {
      panel = new JBPanel();
      JBList<String> list = new JBList<>(refactorings.stream()
          .map(s -> new String(s.getText()))
          .collect(Collectors.toList()));

      MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);
          VcsLogFilterCollection filters = VcsLogFilterObject.collection();
          VcsLogManager.LogWindowKind kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
          kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
          VcsProjectLog.getInstance(project).openLogTab(filters, kind)
              .getVcsLog()
              .jumpToReference(refactorings.get(list.locationToIndex(e.getPoint())).getCommitId());
        }
      };

      MouseAdapter mouseSelection = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
          super.mouseMoved(e);
          e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          list.setSelectedIndex(list.locationToIndex(e.getPoint()));
        }
      };

      list.addMouseMotionListener(mouseSelection);
      list.addMouseListener(mouseAdapter);
      panel.add(list);

      panel.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Refactoring history for "
              + methodName.substring(methodName.lastIndexOf(".") + 1)));
    }

    JBPopup popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(panel, null).createPopup();
    popup.showInBestPositionFor(datacontext);

  }
}
