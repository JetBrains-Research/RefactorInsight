package data;

import static org.refactoringminer.api.RefactoringType.EXTRACT_CLASS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.VcsCommitMetadata;
import data.diffRequests.DiffRequestGenerator;
import data.diffRequests.ThreeSidedDiffRequestGenerator;
import data.diffRequests.TwoSidedDiffRequestGenerator;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RefactoringEntry implements Serializable {

  private static transient final InfoFactory factory = new InfoFactory();
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
//
//  /**
//   * Deserialize a refactoring info json.
//   *
//   * @param value json string
//   * @return a new data.RefactoringInfo object
//   */
//  public static RefactoringEntry fromString(String value) {
//    if (value == null || value.equals("")) {
//      return null;
//    }
//    try {
//      Gson gson = new GsonBuilder()
//          .registerTypeAdapter(DiffFragment.class,
//              InterfaceSerializer.interfaceSerializer(DiffFragmentImpl.class))
//          .registerTypeAdapter(MergeLineFragment.class,
//              InterfaceSerializer.interfaceSerializer(MergeLineFragmentImpl.class))
//          .registerTypeAdapter(LineFragment.class,
//              InterfaceSerializer.interfaceSerializer(LineFragmentImpl.class))
//          .registerTypeAdapter(DiffRequestGenerator.class,
//              InterfaceSerializer.interfaceSerializer(TwoSidedDiffRequestGenerator.class))
//          .create();
//      RefactoringEntry
//          entry = gson.fromJson(value, RefactoringEntry.class);
//      entry.getRefactorings().forEach(r -> r.setEntry(entry));
//      return entry;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//  }

  public static RefactoringEntry fromString(String value) {
    String[] tokens = value.split(",", 4);
    String[] refs = tokens[3].split(",");
    RefactoringEntry entry = new RefactoringEntry(
        tokens[0], tokens[1], Long.parseLong(tokens[2]))
        .setRefactorings(Arrays.stream(refs)
            .map(RefactoringInfo::fromString).collect(Collectors.toList()));
    entry.getRefactorings().forEach(r -> r.setEntry(entry));
    return entry;
  }

  @Override
  public String toString() {
    return commitId + "," + parent + "," + time + "," + refactorings.stream()
        .map(RefactoringInfo::toString).collect(Collectors.joining(","));
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

  static final class InterfaceSerializer<T>
      implements JsonSerializer<T>, JsonDeserializer<T> {

    private final Class<T> implementationClass;

    InterfaceSerializer(final Class<T> implementationClass) {
      this.implementationClass = implementationClass;
    }

    static <T> InterfaceSerializer<T> interfaceSerializer(final Class<T> implementationClass) {
      return new InterfaceSerializer<>(implementationClass);
    }

    @Override
    public JsonElement serialize(final T value, final Type type,
                                 final JsonSerializationContext context) {
      if (value != null && value.getClass().equals(ThreeSidedDiffRequestGenerator.class)) {
        return context.serialize(value, ThreeSidedDiffRequestGenerator.class);
      }
      final Type targetType = value != null
          ? value.getClass()
          : type;
      return context.serialize(value, targetType);
    }

    @Override
    public T deserialize(final JsonElement jsonElement, final Type typeOfT,
                         final JsonDeserializationContext context) {
      try {
        T obj = context.deserialize(jsonElement, implementationClass);
        if (obj.getClass().equals(TwoSidedDiffRequestGenerator.class)) {
          var v = (TwoSidedDiffRequestGenerator) obj;
          if (v.fragments == null) {
            throw new Exception("wrong side number");
          }
        }
        return obj;
      } catch (Exception e) {
        return context.deserialize(jsonElement, ThreeSidedDiffRequestGenerator.class);
      }
    }

  }
}

