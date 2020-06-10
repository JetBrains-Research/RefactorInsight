package data.types.classes;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperClassHandler extends Handler {

  @Override

  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {

    ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;

    if (ref.getExtractedClass().isInterface()) {
      info.setGroup(RefactoringInfo.Group.INTERFACE);
    } else if (ref.getExtractedClass().isAbstract()) {
      info.setGroup(RefactoringInfo.Group.ABSTRACT);
    } else {
      info.setGroup(RefactoringInfo.Group.CLASS);
    }

    return info
        .setDetailsBefore(ref.getExtractedClass().getPackageName())
        .setDetailsAfter(ref.getExtractedClass().getPackageName())
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());
  }
}
