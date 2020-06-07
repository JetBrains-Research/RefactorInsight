package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import java.util.Set;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperClassHandler extends Handler {

  @Override

  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {

    ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;

    Set<String> subclassSet = ref.getSubclassSet().stream()
        .map(x -> x.substring(x.lastIndexOf(".") + 1))
        .collect(Collectors.toSet());

    if (ref.getExtractedClass().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else if (ref.getExtractedClass().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else {
      info.setGroup(Group.CLASS);
    }

    return info
        .setElementBefore(
            (info.getGroup() == Group.INTERFACE ? "implemented by: "
                : "extended by: ")
                + subclassSet.toString().substring(1, subclassSet.toString().length() - 1))
        .setElementAfter(null)
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());
  }
}
