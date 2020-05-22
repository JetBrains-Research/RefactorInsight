package ui;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
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
import services.RefactoringsBundle;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class MethodRefactoringToolbar {

    private ToolWindowManager toolWindowManager;
    private ToolWindow toolWindow;
    private Project project;

    public MethodRefactoringToolbar(Project project) {
        this.project = project;
        toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindow = toolWindowManager
                .registerToolWindow("Refactoring History", true, ToolWindowAnchor.BOTTOM);
    }

    public void showToolbar(List<RefactoringInfo> refactorings, String methodName, DataContext datacontext) {
        JBPanel panel;

        if (refactorings == null || refactorings.isEmpty()) {
            panel = new JBPanel(new GridLayout(0, 1));
            panel.add(new JBLabel(RefactoringsBundle.message("no.ref.method")));
        } else {
            panel = new JBPanel();
            panel.setLayout(new BorderLayout());
            JBSplitter splitterPane = new JBSplitter(false, .5f);
            panel.add(splitterPane);
//            JBList<String> list = new JBList<>(refactorings.stream()
//                    .map(s -> new String(s.getText()))
//                    .collect(Collectors.toList()));


            MethodColumnInfoFactory.project = project;
            ListTableModel<RefactoringInfo> model = new ListTableModel<>(
                    new MethodColumnInfoFactory().getColumnInfos(), refactorings);
            JBTable table = new JBTable(model);
            table.setDefaultRenderer(table.getColumnClass(0), new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    RefactoringInfo method = model.getItem(row);
                    if (column == 0) {
                        setBackground(JBColor.YELLOW);
                    }
                    else {
                        setBackground(JBColor.WHITE);
                    }

                    return super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, column);
                }
            });

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    RefactoringInfo info = refactorings
                            .get(table.convertRowIndexToModel(table.getSelectedRow()));
                    JLabel description = new JLabel(formatText(info.getText()));
                    splitterPane.setSecondComponent(description);
                    if(e.getClickCount() == 2) {
                    VcsLogFilterCollection filters = VcsLogFilterObject.collection();
                    VcsLogManager.LogWindowKind kind = VcsLogManager.LogWindowKind.TOOL_WINDOW;
                        VcsProjectLog.getInstance(project).openLogTab(filters, kind)
                                .getVcsLog()
                                .jumpToReference(info.getCommitId());
                    }
                }
            };

//            list.addMouseMotionListener(mouseSelection);
//            list.addMouseListener(mouseAdapter);
//            panel.add(list);




            table.addMouseListener(mouseAdapter);
            splitterPane.setFirstComponent(new JBScrollPane(table));
            splitterPane.setSecondComponent(new JBLabel("Select refactoring to see description"));

//            panel.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createEtchedBorder(),
//                    String.format(RefactoringsBundle.message("method.refactoring.history"),
//                            methodName.substring(methodName.lastIndexOf(".") + 1))));
        }

        Content content;
        if ((content = toolWindow.getContentManager().findContent(methodName)) != null) {
            content.setComponent(panel);
        } else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            content = contentFactory.createContent(panel, methodName, false);
            toolWindow.getContentManager().addContent(content);
        }
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindow.setIcon(AllIcons.Ide.Rating);
        toolWindow.show();

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
}
