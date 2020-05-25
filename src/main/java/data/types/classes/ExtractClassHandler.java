package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

    //TODO implement three file viewer

    return info.setGroup(Group.CLASS)
        .setElementBefore("from " + ref.getOriginalClass().getName())
        .setElementAfter("extracted " + ref.getExtractedClass().getName())
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());
  }
}
