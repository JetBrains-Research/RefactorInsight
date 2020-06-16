package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperClassHandler extends Handler {

  @Override

  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {

    ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;

    if (ref.getExtractedClass().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else if (ref.getExtractedClass().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else {
      info.setGroup(Group.CLASS);
    }


    info.setDetailsBefore(ref.getExtractedClass().getPackageName())
        .setDetailsAfter(ref.getExtractedClass().getPackageName())
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName())
        .setMoreSided(true)
        .addMarking(null, ref.getExtractedClass().codeRange(), false);

    ref.getUMLSubclassSet().forEach(
        subClass -> info.addMarking(subClass.codeRange(), null, false));

    return info;
  }
}
