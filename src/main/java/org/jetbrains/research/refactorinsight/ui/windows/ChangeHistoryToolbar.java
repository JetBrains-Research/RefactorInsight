package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogPanel;
import icons.RefactorInsightIcons;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;
import org.jetbrains.research.refactorinsight.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;

public class ChangeHistoryToolbar {
    private ToolWindowManager toolWindowManager;
    private ToolWindow toolWindow;
    private Project project;
    private HistoryType type;

    public ChangeHistoryToolbar(Project project) {
        this.project = project;
        toolWindowManager = ToolWindowManager.getInstance(project);
        Utils.manager = toolWindowManager;
        toolWindow =
                toolWindowManager.registerToolWindow(RefactorInsightBundle.message("change.history.toolwindow.name"),
                        true, ToolWindowAnchor.BOTTOM);
    }

    public void showToolbar(String objectName, HistoryType type, List<CodeChange> methodsHistory) {
        this.type = type;
        JBSplitter splitter = new JBSplitter(false, (float) 0.35);
        JBTable table = new JBTable();
        table.setDefaultEditor(Object.class, null);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addColumn(RefactorInsightBundle.message("change.type.column.name"));
        methodsHistory.forEach(i -> model.addRow(new Object[]{i}));
        addMouseListener(splitter, table);
        setFirstComponent(methodsHistory.size(), splitter, table);
        setSecondComponent(splitter);
        showContent(objectName, splitter);
    }

    private void setSecondComponent(JBSplitter splitter) {
        final JBLabel component =
                new JBLabel(RefactorInsightBundle.message("click.to.jump"), SwingConstants.CENTER);
        component.setForeground(Gray._105);
        splitter.setSecondComponent(component);
    }

    private void setFirstComponent(int size, JBSplitter splitter, JBTable table) {
        JBScrollPane pane = new JBScrollPane(table);
        JBLabel label =
                new JBLabel(String.format(RefactorInsightBundle.message("change.detected.count"),
                        size, size > 1 ? "s" : "", type.toString().toLowerCase()));
        label.setForeground(Gray._105);
        pane.setColumnHeaderView(label);
        splitter.setFirstComponent(pane);
    }

    private void addMouseListener(JBSplitter splitter, JBTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CodeChange rowValue = getRowValue(table, 0);
                    if (rowValue == null) {
                        return;
                    }
                    showLogTab(rowValue, splitter);
                }
            }
        });
    }

    private CodeChange getRowValue(JBTable table, int column) {
        int row = table.getSelectedRow();
        CodeChange value = (CodeChange) table.getModel().getValueAt(row, column);
        return value;
    }

    private void showLogTab(CodeChange change, JBSplitter splitter) {
        VcsLogManager logManager = VcsProjectLog.getInstance(project).getLogManager();
        if (logManager == null) {
            return;
        }

        String logId = "method history " + UUID.randomUUID();
        MainVcsLogUi openLogTab = logManager.createLogUi(logManager.getMainLogUiFactory(logId, null),
                VcsLogManager.LogWindowKind.STANDALONE);

        JComponent mainComponent = openLogTab.getMainComponent();
        mainComponent.setAutoscrolls(true);
        mainComponent.setSize(splitter.getSecondComponent().getSize());
        splitter.setSecondComponent(new VcsLogPanel(logManager, openLogTab));

        Utils.disposeWithVcsLogManager(project, () -> {
            setSecondComponent(splitter);
            Disposer.dispose(openLogTab);
        });

        openLogTab.getVcsLog().jumpToReference(change.getCommitId());
    }

    private void showContent(String methodName, JComponent table) {
        Content content;
        if ((content = toolWindow.getContentManager().findContent(methodName)) != null) {
            content.setComponent(table);
        } else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            content = contentFactory.createContent(table, methodName, false);
            toolWindow.getContentManager().addContent(content);
        }

        toolWindow.getContentManager().setSelectedContent(content);
        toolWindow.setIcon(RefactorInsightIcons.toolWindow);
        toolWindow.show();
    }

}
