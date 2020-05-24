package ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import data.RefactoringInfo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import services.RefactoringsBundle;


public class MethodRefactoringToolbar {

  private ToolWindowManager toolWindowManager;
  private ToolWindow toolWindow;
  private Project project;

  /**
   * Constructor for the toolbar.
   *
   * @param project current project
   */
  public MethodRefactoringToolbar(Project project) {
    this.project = project;
    toolWindowManager = ToolWindowManager.getInstance(project);
    toolWindow = toolWindowManager
        .registerToolWindow("Refactoring History", true, ToolWindowAnchor.BOTTOM);
  }

  private static String formatText(String text) {
    StringBuilder sb = new StringBuilder(text);

    int i = 0;
    while ((i = sb.indexOf(" ", i + 60)) != -1) {
      sb.replace(i, i + 1, "<br/>");
    }

    sb.insert(0, "<html>");
    sb.append("</html>");

    return sb.toString();
  }

  /**
   * Displayer for the toolbar.
   *
   * @param refactorings detected refactorings
   * @param methodName   name of the method
   */
  public void showToolbar(List<RefactoringInfo> refactorings,
                          String methodName) {
    JBPanel panel;

    if (refactorings == null || refactorings.isEmpty()) {
      panel = new JBPanel(new GridLayout(0, 1));
      panel.add(new JBLabel(RefactoringsBundle.message("no.ref.method")));
    } else {
      panel = new JBPanel();
      panel.setLayout(new BorderLayout());
      JBSplitter splitterPane = new JBSplitter(false, .75f);
      panel.add(splitterPane);


      MethodColumnInfoFactory.project = project;
      ListTableModel<RefactoringInfo> model = new ListTableModel<>(
          new MethodColumnInfoFactory().getColumnInfos(), refactorings);
      JBTable table = new JBTable(model);

      MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);
          RefactoringInfo info = refactorings
              .get(table.convertRowIndexToModel(table.getSelectedRow()));
          JLabel description = new JLabel(formatText(info.getText()));
          splitterPane.setSecondComponent(description);
          if (e.getClickCount() == 2) {
            VcsLogFilterCollection filters = VcsLogFilterObject.collection();
            VcsLogManager.LogWindowKind kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
            VcsProjectLog.getInstance(project).openLogTab(filters, kind)
                .getVcsLog()
                .jumpToReference(info.getCommitId());
          }
        }
      };


      table.addMouseListener(mouseAdapter);
      splitterPane.setFirstComponent(new JBScrollPane(table));
      splitterPane.setSecondComponent(
          new JBLabel(RefactoringsBundle.message("method.refactoring.history"),
              SwingConstants.CENTER));
    }

    Content content;
    if ((content = toolWindow.getContentManager().findContent(methodName)) != null) {
      content.setComponent(panel);
    } else {
      ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
      content = contentFactory
          .createContent(panel, methodName.substring(methodName.lastIndexOf(".") + 1), false);
      toolWindow.getContentManager().addContent(content);
    }
    toolWindow.getContentManager().setSelectedContent(content);
    toolWindow.setIcon(AllIcons.Ide.Rating);
    toolWindow.show();

  }
}
