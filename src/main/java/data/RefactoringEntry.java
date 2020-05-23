package data;

import com.google.gson.Gson;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.tree.ui.DefaultTreeUI;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jetbrains.annotations.Nullable;
import org.refactoringminer.api.Refactoring;

public class RefactoringEntry implements Serializable {

  private static InfoFactory factory = new InfoFactory();
  private List<RefactoringInfo> data;
  private List<String> parents;
  private String commitId;
  private long time;

  /**
   * Constructor for method refactoring.
   *
   * @param data    the refactoring data.
   * @param parents the commit ids of the parents.
   * @param time    timestamp of the commit.
   */
  public RefactoringEntry(List<RefactoringInfo> data, String commitId, List<String> parents,
                          long time) {
    this.data = data;
    this.parents = parents;
    this.time = time;
    this.commitId = commitId;
    data.forEach(r -> r.setEntry(this));
  }

  /**
   * Deserialize a refactoring info json.
   *
   * @param value json string
   * @return a new data.RefactoringInfo object
   */
  public static RefactoringEntry fromString(String value) {
    if (value == null || value.equals("")) {
      return null;
    }
    try {
      RefactoringEntry entry = new Gson().fromJson(value, RefactoringEntry.class);
      entry.getRefactorings().forEach(r -> r.setEntry(entry));
      return entry;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Converter to Json.
   *
   * @param refactorings to be processed.
   * @param commitId     current commit.
   * @param parents      parent ids of the current commit.
   * @param time         timestamp of the current commit.
   * @return Json string.
   */
  public static String convert(List<Refactoring> refactorings, String commitId,
                               List<String> parents, long time) {
    return new RefactoringEntry(
        refactorings.stream()
            .map(refactoring -> factory.create(refactoring))
            .collect(Collectors.toList()),
        commitId, parents, time).toString();
  }

  public List<RefactoringInfo> getRefactorings() {
    return data;
  }

  public List<String> getParents() {
    return parents;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public long getTimeStamp() {
    return time;
  }

  public String getCommitId() {
    return commitId;
  }

  /**
   * Builds a UI tree.
   *
   * @return Swing Tree visualisation of refactorings in this entry.
   */
  public Tree buildTree() {
    List<RefactoringInfo> refs = data;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Refactorings at commit " + commitId);

    for (RefactoringInfo refactoringInfo : refs) {
      DefaultMutableTreeNode refName =
          new DefaultMutableTreeNode(refactoringInfo);
      root.add(refName);
      char a = '→';
      DefaultMutableTreeNode change = new
          DefaultMutableTreeNode(
          refactoringInfo.getNameBefore() + " " + a + " " + refactoringInfo.getNameAfter());
      refName.add(change);
    }
    Tree tree = new Tree(root);

    tree.setRootVisible(true);

    MyCellRenderer renderer = new MyCellRenderer();
    tree.setCellRenderer(renderer);

    return tree;
  }
}
