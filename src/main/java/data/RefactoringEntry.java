package data;

import com.google.gson.Gson;
import com.intellij.ui.treeStructure.Tree;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;

public class RefactoringEntry implements Serializable {

  private static InfoFactory factory = new InfoFactory();
  private List<RefactoringInfo> refactorings;
  private List<String> parents;
  private String commitId;
  private long time;

  /**
   * Constructor for method refactoring.
   *
   * @param refactorings the refactoring data.
   * @param parents      the commit ids of the parents.
   * @param time         timestamp of the commit.
   */
  public RefactoringEntry(List<RefactoringInfo> refactorings, String commitId, List<String> parents,
                          long time) {
    this.refactorings = refactorings;
    this.parents = parents;
    this.time = time;
    this.commitId = commitId;
    refactorings.forEach(r -> r.setEntry(this));
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

  /**
   * Builds a UI tree.
   *
   * @return Swing Tree visualisation of refactorings in this entry.
   */
  public Tree buildTree() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(commitId);
    refactorings.forEach(r -> root.add(r.makeNode()));

    Tree tree = new Tree(root);
    tree.setRootVisible(false);
    expandAllNodes(tree, 0, tree.getRowCount());
    MyCellRenderer renderer = new MyCellRenderer();
    tree.setCellRenderer(renderer);
    return tree;
  }

  private void expandAllNodes(Tree tree, int startingIndex, int rowCount) {
    for (int i = startingIndex; i < rowCount; ++i) {
      tree.expandRow(i);
    }
    if (tree.getRowCount() != rowCount) {
      expandAllNodes(tree, rowCount, tree.getRowCount());
    }
  }

  public List<RefactoringInfo> getRefactorings() {
    return refactorings;
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
}
