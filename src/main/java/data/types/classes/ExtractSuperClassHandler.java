package data.types.classes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractSuperClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;
    //TODO recent location thingy
    return info.setGroup(Group.CLASS)
        .setElementBefore("extracted " + ref.getExtractedClass().getName())
        .setElementAfter(null)
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());
  }
}
