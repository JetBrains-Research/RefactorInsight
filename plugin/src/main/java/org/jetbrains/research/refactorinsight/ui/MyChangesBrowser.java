package org.jetbrains.research.refactorinsight.ui;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionExtensionProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.RemoteRevisionsCache;
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserBase;
import com.intellij.openapi.vcs.changes.ui.RemoteStatusChangeNodeDecorator;
import com.intellij.openapi.vcs.changes.ui.TreeModelBuilder;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Collection;

public class MyChangesBrowser extends ChangesBrowserBase {

    private final Project project;

    @Override
    protected void init() {
        super.init();
    }

    protected MyChangesBrowser(@NotNull Project project, boolean showCheckboxes, boolean highlightProblems) {
        super(project, showCheckboxes, highlightProblems);
        this.project = project;
    }

    @Override
    protected @NotNull DefaultTreeModel buildTreeModel() {
        Collection<Change> allChanges =  ChangeListManager.getInstance(project).getAllChanges();
        RemoteStatusChangeNodeDecorator decorator = RemoteRevisionsCache.getInstance(myProject).getChangesNodeDecorator();
        return TreeModelBuilder.buildFromChanges(myProject, getGrouping(), allChanges, decorator);
    }

    @Override
    protected @Nullable JComponent createHeaderPanel() {
        JBTabs tabs = new JBTabsImpl(project);
        JPanel firstTabPanel = new JPanel();
        firstTabPanel.add(new JLabel("First Tab"));
        TabInfo firstTabInfo = new TabInfo(firstTabPanel);
        firstTabInfo.setText("First Tab");

        tabs.addTab(firstTabInfo);

        JPanel secondTabPanel = new JPanel();
        secondTabPanel.add(new JLabel("Second Tab"));
        TabInfo secondTabInfo = new TabInfo(secondTabPanel);
        secondTabInfo.setText("Second Tab");

        tabs.addTab(secondTabInfo);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabs.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    public static class ShowStandaloneDiff implements AnActionExtensionProvider {

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public boolean isActive(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            ChangesBrowserBase changesBrowser = e.getData(DATA_KEY);
            return project != null && changesBrowser != null && changesBrowser.canShowDiff();
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            ChangesBrowserBase changesBrowser = e.getRequiredData(DATA_KEY);
            Project project = e.getRequiredData(CommonDataKeys.PROJECT);

            showStandaloneDiff(project, changesBrowser);
        }
    }
}
