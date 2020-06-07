package data;

import static org.refactoringminer.api.RefactoringType.CHANGE_ATTRIBUTE_TYPE;
import static org.refactoringminer.api.RefactoringType.CHANGE_VARIABLE_TYPE;
import static org.refactoringminer.api.RefactoringType.EXTRACT_CLASS;
import static org.refactoringminer.api.RefactoringType.RENAME_ATTRIBUTE;

import com.google.gson.Gson;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
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
  public RefactoringEntry(String commitId, List<String> parents, long time) {
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
  public static String convert(List<Refactoring> refactorings, VcsCommitMetadata commit) {
    List<String> parents =
        commit.getParents().stream().map(Hash::asString).collect(Collectors.toList());

    RefactoringEntry entry =
        new RefactoringEntry(commit.getId().asString(), parents, commit.getTimestamp());

    List<RefactoringInfo> infos =
        refactorings.stream().map(ref -> factory.create(ref, entry)).collect(
            Collectors.toList());

    entry.setRefactorings(infos).combineRelated();
    return entry.toString();
  }

  private void combineRelated() {
    combineRelatedExtractClass();

    HashMap<String, List<RefactoringInfo>> groups = new HashMap<>();
    refactorings.forEach(r -> {
      if (r.getGroupId() != null) {
        List<RefactoringInfo> list = groups.getOrDefault(r.getGroupId(), new ArrayList<>());
        list.add(r);
        groups.put(r.getGroupId(), list);
      }
      r.setEntry(this);
    });

    groups.forEach((k, v) -> {
      if (v.size() > 1) {
        RefactoringInfo info = getMainRefactoringInfo(v);

        v.remove(info);
        v.forEach(r -> {
          info.addIncludedRefactoring(r.getName());
          info.addAllMarkings(r);
          r.setHidden(true);
        });
      }
    });
  }

  private void combineRelatedExtractClass() {
    List<RefactoringInfo> extractClassRefactorings = refactorings
        .stream().filter(x -> x.getType() == EXTRACT_CLASS).collect(Collectors.toList());
    for (RefactoringInfo extractClass : extractClassRefactorings) {
      String displayableElement = Utils
          .getDisplayableElement(extractClass.getElementBefore(), extractClass.getElementAfter());
      refactorings.stream().filter(x -> !x.equals(extractClass))
          .filter(x -> {
            String displayableDetails =
                Utils.getDisplayableElement(x.getDetailsBefore(), x.getDetailsAfter());
            String displayableName =
                Utils.getDisplayableElement(x.getNameBefore(), x.getNameAfter());
            if (displayableDetails == null) {
              return displayableName.equals(displayableElement);
            }
            return (displayableDetails.equals(displayableElement)
                || displayableName.equals(displayableElement));
          })
          .forEach(r -> {
            extractClass.addIncludedRefactoring(r.getName());
            r.setHidden(true);
          });
    }
  }

  private RefactoringInfo getMainRefactoringInfo(List<RefactoringInfo> v) {
    RefactoringInfo info = null;
    if (v.stream().anyMatch(ofType(RENAME_ATTRIBUTE))
        && v.stream().anyMatch(ofType(CHANGE_ATTRIBUTE_TYPE))) {
      info = v.stream().filter(ofType(RENAME_ATTRIBUTE)).findFirst().get();
      info.setName("Rename and Change Attribute Type");
    } else if (v.stream().anyMatch(ofType(RENAME_ATTRIBUTE))) {
      info = v.stream().filter(ofType(RENAME_ATTRIBUTE)).findFirst().get();
      info.setName("Rename Attribute");
    } else if (v.stream().anyMatch(ofType(CHANGE_ATTRIBUTE_TYPE))) {
      info = v.stream().filter(ofType(CHANGE_ATTRIBUTE_TYPE)).findFirst().get();
      info.setName("Change Attribute Type");
    } else if (v.stream().anyMatch(ofType(CHANGE_VARIABLE_TYPE))) {
      info = v.stream().filter(ofType(CHANGE_VARIABLE_TYPE)).findFirst().get();
      info.setName("Rename and Change Variable Type");
    }
    return info;
  }

  private Predicate<RefactoringInfo> ofType(RefactoringType type) {
    return (r) -> r.getType() == type;
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
        root.add(Utils.makeNode(r));
      }
    });
    Tree tree = new Tree(root);
    tree.setRootVisible(false);
    Utils.expandAllNodes(tree, 0, tree.getRowCount());
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
