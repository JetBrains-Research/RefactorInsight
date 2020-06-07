package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyAttributeAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class ModifyAttributeAnnotationHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyAttributeAnnotationRefactoring ref = (ModifyAttributeAnnotationRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(ref.getAttributeBefore().toQualifiedString())
        .setNameAfter(ref.getAttributeAfter().toQualifiedString())
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange(),
            line -> line.addOffset(ref.getAnnotationBefore().getLocationInfo(),
                ref.getAnnotationAfter().getLocationInfo()), true);
  }
}
