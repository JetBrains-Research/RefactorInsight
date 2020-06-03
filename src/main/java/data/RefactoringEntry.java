package data;

import static org.refactoringminer.api.RefactoringType.CHANGE_ATTRIBUTE_TYPE;
import static org.refactoringminer.api.RefactoringType.CHANGE_VARIABLE_TYPE;
import static org.refactoringminer.api.RefactoringType.RENAME_ATTRIBUTE;

import com.google.gson.Gson;
import com.intellij.ui.treeStructure.Tree;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

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
    HashMap<String, List<RefactoringInfo>> map = new HashMap<>();
    refactorings.forEach(r -> {
      if (r.getGroupId() != null) {
        List<RefactoringInfo> list = map.getOrDefault(r.getGroupId(), new ArrayList<>());
        list.add(r);
        map.put(r.getGroupId(), list);
      }
      r.setEntry(this);
    });

    map.forEach((k, v) -> {
      if (v.size() > 1) {
        RefactoringInfo info = getMainRefactoringInfo(v);
        v.remove(info);
        v.forEach(r -> {
          info.addAllMarkings(r);
          r.setHidden(true);
        });
      }
    });
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

  private RefactoringInfo getMainRefactoringInfo(List<RefactoringInfo> v) {
    RefactoringInfo info = null;
    if (v.stream().filter(x1 -> x1.getType() == RENAME_ATTRIBUTE).count() > 0 && v.stream().filter(
        x2 -> x2.getType() == CHANGE_ATTRIBUTE_TYPE)
        .count() > 0) {
      info = v.stream().filter(x -> x.getType() == RENAME_ATTRIBUTE).collect(Collectors.toList())
          .get(0);
      info.setName("Change Attribute Type & Rename Attribute");
    } else if (v.stream().filter(x1 -> x1.getType() == RENAME_ATTRIBUTE).count() > 0) {
      info = v.stream().filter(x -> x.getType() == RENAME_ATTRIBUTE).collect(Collectors.toList())
          .get(0);
      info.setName("Rename Attribute");
    } else if (v.stream().filter(x2 -> x2.getType() == CHANGE_ATTRIBUTE_TYPE).count() > 0) {
      info =
          v.stream().filter(x -> x.getType() == CHANGE_ATTRIBUTE_TYPE).collect(Collectors.toList())
              .get(0);
      info.setName("Change Attribute Type");
    } else {
      if (v.stream().filter(x -> x.getType() == CHANGE_VARIABLE_TYPE).count() > 0) {
        info =
            v.stream().filter(x -> x.getType() == CHANGE_VARIABLE_TYPE).collect(Collectors.toList())
                .get(0);
        info.setName("Change Variable Type & Rename Variable");
      }
    }
    return info;
  }

  /**
   * Builds a UI tree.
   *
   * @return Swing Tree visualisation of refactorings in this entry.
   */
  public Tree buildTree() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(commitId);
    refactorings.forEach(r -> {
      if (!r.isHidden()) {
        root.add(r.makeNode());
      }
    });

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
