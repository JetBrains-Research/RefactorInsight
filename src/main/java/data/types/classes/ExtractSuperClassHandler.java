package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    //TODO three file viewer
    ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;
    return info.setGroup(Group.CLASS)
        .setElementBefore("extracted " + ref.getExtractedClass().getName())
        .setElementAfter(null)
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());
  }
}
