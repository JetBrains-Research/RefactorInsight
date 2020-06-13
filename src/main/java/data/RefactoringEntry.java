package data;

import static org.refactoringminer.api.RefactoringType.EXTRACT_CLASS;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RefactoringEntry implements Serializable {

  private static final transient InfoFactory factory = new InfoFactory();
  private final String commitId;
  private final String parent;
  private final long time;
  private List<RefactoringInfo> refactorings;

  /**
   * Constructor for method refactoring.
   *
   * @param parent the commit id of the parent.
   * @param time   timestamp of the commit.
   */
  public RefactoringEntry(String commitId, String parent, long time) {
    this.commitId = commitId;
    this.parent = parent;
    this.time = time;
  }

  public static RefactoringEntry fromString(String value) {
    String[] tokens = value.split(Utils.ENTRY_DELIMITER, 4);
    String[] refs = tokens[3].split(Utils.ENTRY_DELIMITER);
    if (refs[0].isEmpty()) {
      refs = new String[0];
    }
    RefactoringEntry entry = new RefactoringEntry(
        tokens[0], tokens[1], Long.parseLong(tokens[2]))
        .setRefactorings(Arrays.stream(refs)
            .map(RefactoringInfo::fromString).collect(Collectors.toList()));
    entry.getRefactorings().forEach(r -> r.setEntry(entry));
    return entry;
  }

  @Override
  public String toString() {
    return commitId + Utils.ENTRY_DELIMITER + parent + Utils.ENTRY_DELIMITER
        + time + Utils.ENTRY_DELIMITER + refactorings.stream()
        .map(RefactoringInfo::toString).collect(Collectors.joining(Utils.ENTRY_DELIMITER));
  }

  /**
   * Converter to Json.
   *
   * @param refactorings to be processed.
   * @param commit       current commit.
   * @return Json string.
   */
  public static RefactoringEntry convert(List<Refactoring> refactorings, VcsCommitMetadata commit,
                                         Project project) {

    RefactoringEntry entry =
        new RefactoringEntry(commit.getId().asString(),
            commit.getParents().get(0).asString(), commit.getTimestamp());

    List<RefactoringInfo> infos =
        refactorings.stream().map(ref -> factory.create(ref, entry)).collect(
            Collectors.toList());

    entry.setRefactorings(infos).combineRelated();

    entry.refactorings.forEach(info -> Utils.check(info, project));
    return entry;
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
        RefactoringInfo info = Utils.getMainRefactoringInfo(v);

        if (info == null) {
          System.out.println("Grouping failed");
          return;
        }

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

  public String getParent() {
    return parent;
  }

  public long getTimeStamp() {
    return time;
  }

  public String getCommitId() {
    return commitId;
  }

}

