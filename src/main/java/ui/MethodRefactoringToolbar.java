package ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import data.MyCellRenderer;
import data.RefactoringInfo;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import services.RefactoringsBundle;
import utils.Utils;


public class MethodRefactoringToolbar {

  private final VcsLogManager.VcsLogUiFactory<? extends MainVcsLogUi> factory;
  private MainVcsLogUi openLogTab;
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

    factory = VcsProjectLog.getInstance(project).getLogManager()
        .getMainLogUiFactory("method history", VcsLogFilterObject.collection());
    toolWindow =
        toolWindowManager.registerToolWindow("Refactoring History", true, ToolWindowAnchor.BOTTOM);
  }

  /**
   * Display the toolbar.
   *
   * @param refactorings detected refactorings
   * @param methodName   name of the method
   */
  public void showToolbar(List<RefactoringInfo> refactorings,
                          String methodName, DataContext datacontext) {

    if (refactorings == null || refactorings.isEmpty()) {
      showPopup(datacontext);
    } else {
      JBSplitter splitter = new JBSplitter(false, (float) 0.3);
      Tree tree = createTree(refactorings);
      tree.setRootVisible(false);
      Utils.expandAllNodes(tree, 0, tree.getRowCount());
      tree.setCellRenderer(new MyCellRenderer(true));
      addMouseListener(splitter, tree);
      setFirstComponent(refactorings.size(), splitter, tree);
      setSecondComponent(splitter);
      showContent(methodName, splitter);
    }
  }

  private void setSecondComponent(JBSplitter splitter) {
    final JBLabel component =
        new JBLabel("Double click to jump at commit.", SwingConstants.CENTER);
    component.setForeground(Gray._105);
    splitter.setSecondComponent(component);
  }

  private void setFirstComponent(int size, JBSplitter splitter, Tree tree) {
    JBScrollPane pane = new JBScrollPane(tree);
    JBLabel label =
        new JBLabel(
            size + (size > 1 ? " refactorings" : " refactoring") + " detected for this method");
    label.setForeground(Gray._105);
    pane.setColumnHeaderView(label);
    splitter.setFirstComponent(pane);
  }

  private void addMouseListener(JBSplitter splitter, Tree tree) {
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          TreePath path = tree.getPathForLocation(e.getX(), e.getY());
          if (path == null) {
            return;
          }
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)
              path.getLastPathComponent();
          if (node.isLeaf()) {
            RefactoringInfo info = (RefactoringInfo) node.getUserObjectPath()[1];
            showLogTab(info, splitter);
          }
        }
      }
    });
  }

  private void showLogTab(RefactoringInfo info, JBSplitter splitter) {
    VcsLogData data = VcsProjectLog.getInstance(project).getLogManager().getDataManager();
    openLogTab = factory.createLogUi(project, data);
    Utils.add(openLogTab);
    JComponent mainComponent = openLogTab.getMainComponent();
    if (mainComponent != null) {
      mainComponent.setAutoscrolls(true);
      mainComponent.setSize(splitter.getSecondComponent().getSize());
      splitter.setSecondComponent(mainComponent);
      openLogTab.jumpToHash(info.getCommitId());
    }
  }

  @NotNull
  private Tree createTree(List<RefactoringInfo> refactorings) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    for (RefactoringInfo refactoringInfo : refactorings) {
      if (refactoringInfo.getDisplayableName().contains("->")) {
        DefaultMutableTreeNode node = refactoringInfo.makeNode();
        root.add(node);
      } else {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(refactoringInfo);
        refactoringInfo.addLeaves(node);
        root.add(node);
      }
    }
    return new Tree(root);
  }

  private void showContent(String methodName, JComponent tree) {
    Content content;
    if ((content = toolWindow.getContentManager()
        .findContent(methodName.substring(methodName.lastIndexOf(".") + 1))) != null) {
      content.setComponent(tree);
    } else {
      ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
      content = contentFactory
          .createContent(tree, methodName.substring(methodName.lastIndexOf(".") + 1), false);
      toolWindow.getContentManager().addContent(content);
    }
    toolWindow.getContentManager().setSelectedContent(content);
    toolWindow.setIcon(AllIcons.Actions.RefactoringBulb);
    toolWindow.show();
  }

  private void showPopup(DataContext datacontext) {
    JBPanel panel = new JBPanel(new GridLayout(0, 1));
    panel.add(new JBLabel(RefactoringsBundle.message("no.ref.method")));
    JBPopup popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(panel, null).createPopup();
    popup.showInBestPositionFor(datacontext);
  }
}