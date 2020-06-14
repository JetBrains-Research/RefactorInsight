package ui.windows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.frame.MainFrame;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import data.RefactoringInfo;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import services.RefactoringsBundle;
import ui.tree.TreeUtils;
import ui.tree.renderers.HistoryToolbarRenderer;
import utils.Utils;

public class RefactoringHistoryToolbar {

  private final VcsLogManager.VcsLogUiFactory<? extends MainVcsLogUi> factory;

  private MainVcsLogUi openLogTab;
  private ToolWindowManager toolWindowManager;
  private ToolWindow toolWindow;
  private Project project;
  private HistoryType type;

  /**
   * Constructor for the toolbar.
   *
   * @param project current project
   */
  public RefactoringHistoryToolbar(Project project) {
    this.project = project;
    toolWindowManager = ToolWindowManager.getInstance(project);
    Utils.manager = toolWindowManager;
    factory = VcsProjectLog.getInstance(project).getLogManager()
        .getMainLogUiFactory("method history", VcsLogFilterObject.collection());
    toolWindow =
        toolWindowManager.registerToolWindow("Refactoring History", true, ToolWindowAnchor.BOTTOM);

  }

  /**
   * Display the toolbar.
   *
   * @param refactorings detected refactorings
   * @param objectsName  name of the method
   */
  public void showToolbar(List<RefactoringInfo> refactorings,
                          String objectsName, DataContext datacontext, HistoryType type,
                          @Nullable HashMap<String, ArrayList<RefactoringInfo>> methodsHistory,
                          @Nullable HashMap<String, ArrayList<RefactoringInfo>> attributesHistory) {

    this.type = type;
    if (refactorings == null || refactorings.isEmpty()) {
      showPopup(datacontext);
    } else {
      JBSplitter splitter = new JBSplitter(false, (float) 0.35);
      List<RefactoringInfo> infos =
          refactorings.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());

      Utils.chronologicalOrder(infos);

      Tree tree =
          createTree(infos, methodsHistory, attributesHistory);
      tree.setRootVisible(false);
      //TreeUtils.expandAllNodes(tree, 0, tree.getRowCount());
      tree.setCellRenderer(new HistoryToolbarRenderer());
      addMouseListener(splitter, tree);
      setFirstComponent(infos.size(), splitter, tree);
      setSecondComponent(splitter);
      showContent(objectsName, splitter);
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
            size + (size > 1 ? " refactorings" : " refactoring") + " detected for this "
                + type.toString().toLowerCase());
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
            RefactoringInfo info = HistoryToolbarRenderer.getRefactoringInfo(node);
            if (info == null) {
              return;
            }
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

      JBSplitter splitter1 = (JBSplitter) mainComponent.getComponent(0);

      BorderLayoutPanel splitter2 = (BorderLayoutPanel) splitter1.getFirstComponent();

      OnePixelSplitter panel = (OnePixelSplitter) splitter2.getComponent(1);
      MainFrame mainFrame = (MainFrame) panel.getComponent(2);

      OnePixelSplitter splitter3 = (OnePixelSplitter) mainFrame.getComponent(0);
      OnePixelSplitter splitter4 = (OnePixelSplitter) splitter3.getComponent(2);
      JBLoadingPanel panel1 = (JBLoadingPanel) splitter4.getComponent(1);
      JComponent loadingDecorator = (JComponent) panel1.getComponent(0);
      JPanel panel2 = (JPanel) loadingDecorator.getComponent(0);
      VcsLogChangesBrowser browser = (VcsLogChangesBrowser) panel2.getComponent(0);
      ((ActionButton) browser.getToolbar().getComponent().getComponent(1)).click();
    }
  }

  @NotNull
  private Tree createTree(List<RefactoringInfo> refactorings,
                          HashMap<String, ArrayList<RefactoringInfo>> methods,
                          HashMap<String, ArrayList<RefactoringInfo>> attributes) {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    createRefactoringsTree(refactorings, root);

    AtomicInteger expandable = new AtomicInteger();
    root.breadthFirstEnumeration().asIterator().forEachRemaining((c) -> expandable
        .getAndIncrement());

    if (methods != null && !methods.isEmpty()) {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode("Check methods in this class");
      addObjectsToTree(methods, child, true);
      if (child.getChildCount() > 0) {
        root.add(child);
      }
    }

    if (attributes != null && !attributes.isEmpty()) {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode("Check fields in this class");
      addObjectsToTree(attributes, child, false);
      if (child.getChildCount() > 0) {
        root.add(child);
      }
    }

    Tree tree = new Tree(root);
    for (int i = 0; i < expandable.get(); i++) {
      tree.expandRow(i);
    }
    if (tree.getRowCount() > expandable.get()) {
      int history1 = expandable.get();
      tree.expandRow(history1);
      DefaultMutableTreeNode n =
          (DefaultMutableTreeNode) tree.getPathForRow(history1).getLastPathComponent();
      if (tree.getRowCount() > history1 + n.getChildCount() + 1) {
        tree.expandRow(history1 + n.getChildCount() + 1);
      }
    }
    return tree;
  }

  private void addObjectsToTree(HashMap<String, ArrayList<RefactoringInfo>> objects,
                                DefaultMutableTreeNode child, boolean forMethods) {
    objects.forEach((obj, refs) -> {
      if (!refs.isEmpty()) {
        DefaultMutableTreeNode m =
            new DefaultMutableTreeNode(forMethods
                ? obj.substring(obj.lastIndexOf(".") + 1)
                : obj.substring(obj.lastIndexOf("|") + 1));
        List<RefactoringInfo> infoList =
            refs.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
        Utils.chronologicalOrder(infoList);
        createRefactoringsTree(infoList, m);
        child.add(m);
      }
    });
  }

  private void createRefactoringsTree(List<RefactoringInfo> refactorings,
                                      DefaultMutableTreeNode root) {
    for (RefactoringInfo ref : refactorings) {
      TreeUtils.createHistoryTree(root, ref);
    }
  }

  private void showContent(String methodName, JComponent tree) {
    Content content;
    if ((content = toolWindow.getContentManager().findContent(methodName)) != null) {
      content.setComponent(tree);
    } else {
      ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
      content = contentFactory.createContent(tree, methodName, false);
      toolWindow.getContentManager().addContent(content);
    }
    toolWindow.getContentManager().setSelectedContent(content);
    toolWindow.setIcon(AllIcons.Actions.RefactoringBulb);
    toolWindow.show();
  }

  private void showPopup(DataContext datacontext) {
    JBPanel panel = new JBPanel(new GridLayout(0, 1));
    panel.add(new JBLabel(RefactoringsBundle.message("no.ref.history")));
    JBPopup popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(panel, null).createPopup();
    popup.showInBestPositionFor(datacontext);
  }

}