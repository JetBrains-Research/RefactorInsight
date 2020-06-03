package data;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RefactoringEntry implements Serializable {

  private static final InfoFactory factory = new InfoFactory();
  private final List<String> parents;
  private final String commitId;
  private final long time;
  private List<RefactoringInfo> refactorings;

  /**
   * Constructor for method refactoring.
   *
   * @param parents the commit ids of the parents.
   * @param time    timestamp of the commit.
   */
  public RefactoringEntry(String commitId, List<String> parents,
                          long time) {
    this.parents = parents;
    this.time = time;
    this.commitId = commitId;
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
   * @param commit       current commit.
   * @return Json string.
   */
  public static String convert(List<Refactoring> refactorings, VcsCommitMetadata commit,
                               Project project) {
    List<String> parents =
        commit.getParents().stream().map(Hash::asString).collect(Collectors.toList());

    RefactoringEntry entry =
        new RefactoringEntry(commit.getId().asString(), parents, commit.getTimestamp());

    List<RefactoringInfo> infos =
        refactorings.stream().map(ref -> factory.create(ref, entry, project)).collect(
            Collectors.toList());

    return entry.setRefactorings(infos).toString();
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
    Utils.expandAllNodes(tree, 0, tree.getRowCount());
    MyCellRenderer renderer = new MyCellRenderer();
    tree.setCellRenderer(renderer);
    return tree;
  }

  public List<RefactoringInfo> getRefactorings() {
    return refactorings;
  }

  public RefactoringEntry setRefactorings(List<RefactoringInfo> refactorings) {
    this.refactorings = refactorings;
    return this;
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
