package data;

import static org.refactoringminer.api.RefactoringType.EXTRACT_CLASS;
import static org.refactoringminer.api.RefactoringType.EXTRACT_SUPERCLASS;
import static org.refactoringminer.api.RefactoringType.PULL_UP_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.PULL_UP_OPERATION;
import static utils.StringUtils.ENTRY;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import ui.tree.TreeUtils;
import utils.StringUtils;
import utils.Utils;

public class RefactoringEntry implements Serializable {

  private static final transient InfoFactory factory = new InfoFactory();

  private final transient String commitId;
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

  /**
   * Deserializes a RefactoringEntry.
   *
   * @param value String
   * @return the RefactoringEntry
   */
  public static RefactoringEntry fromString(String value, String commitId) {
    String regex = StringUtils.delimiter(ENTRY, true);
    String[] tokens = value.split(regex, 3);
    String[] refs = tokens[2].split(regex);
    if (refs[0].isEmpty()) {
      refs = new String[0];
    }
    RefactoringEntry entry = new RefactoringEntry(
        commitId, tokens[0], Long.parseLong(tokens[1]))
        .setRefactorings(Arrays.stream(refs)
            .map(RefactoringInfo::fromString).collect(Collectors.toList()));
    entry.getRefactorings().forEach(r -> r.setEntry(entry));
    return entry;
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

  @Override
  public String toString() {
    String del = StringUtils.delimiter(ENTRY);
    return parent + del + time + del + refactorings.stream()
        .map(RefactoringInfo::toString).collect(Collectors.joining(del));
  }

  private void combineRelated() {
    combineRelatedExtractSuperClass();
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

  private void combineRelatedExtractSuperClass() {
    //Find all extract Super Class Refactorings
    List<RefactoringInfo> superClassRefs = refactorings.stream()
        .filter(info -> info.getType() == EXTRACT_SUPERCLASS)
        .collect(Collectors.toList());

    //Find all pull up refactorings
    List<RefactoringInfo> pullUpRefs = refactorings.stream()
        .filter(info -> info.getType() == PULL_UP_ATTRIBUTE || info.getType() == PULL_UP_OPERATION)
        .collect(Collectors.toList());


    superClassRefs.forEach(info -> {
      String superPath = info.getRightPath();
      //Relate
      List<RefactoringInfo> related = pullUpRefs.stream()
          .filter(pullUp -> pullUp.getRightPath().equals(superPath))
          .collect(Collectors.toList());

      //Combine ranges
      related.forEach(relInfo -> {
        relInfo.setHidden(true);
        relInfo.getLineMarkings().forEach(line -> {
          line.setMoreSided(true);
          info.getLineMarkings().add(0, line);
          info.getMoreSidedLeftPaths().add(0, new Pair<>(relInfo.getLeftPath(), false));
        });
      });
    });

  }

  private void combineRelatedExtractClass() {
    List<RefactoringInfo> extractClassRefactorings = refactorings
        .stream().filter(x -> x.getType() == EXTRACT_CLASS).collect(Collectors.toList());
    for (RefactoringInfo extractClass : extractClassRefactorings) {
      String displayableElement = TreeUtils
          .getDisplayableElement(extractClass.getElementBefore(), extractClass.getElementAfter());
      refactorings.stream().filter(x -> !x.equals(extractClass))
          .filter(x -> {
            String displayableDetails =
                TreeUtils.getDisplayableElement(x.getDetailsBefore(), x.getDetailsAfter());
            String displayableName =
                TreeUtils.getDisplayableElement(x.getNameBefore(), x.getNameAfter());
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

