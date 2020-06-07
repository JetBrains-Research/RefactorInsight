package data.types.classes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class ExtractClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

    info.setGroup(Group.CLASS)
        .setElementBefore(ref.getOriginalClass().getName())
        .setElementAfter(ref.getExtractedClass().getName())
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName())
        .setThreeSided(true);

    if (ref.getAttributeOfExtractedClassTypeInOriginalClass() != null) {
      ref.getExtractedOperations().forEach(operation -> {
        info.addMarking(
            operation.codeRange(),
            ref.getExtractedClass().codeRange(),
            ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(),
            RefactoringLine.VisualisationType.LEFT,
            true);
      });

      ref.getExtractedAttributes().forEach(operation -> {
        info.addMarking(
            operation.codeRange(),
            ref.getExtractedClass().codeRange(),
            ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(),
            RefactoringLine.VisualisationType.LEFT,
            true);
      });


      String[] nameSpace = ref.getExtractedClass().getName().split("\\.");
      String className = nameSpace[nameSpace.length - 1];

      info.addMarking(
          ref.getOriginalClass().codeRange(),
          ref.getExtractedClass().codeRange(),
          ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(),

          RefactoringLine.VisualisationType.RIGHT,
          refactoringLine -> {
            refactoringLine.setLazyNames(new String[]{
                null,
                className,
                null
            });
          },
          RefactoringLine.MarkingOption.EXTRACT,
          false);
    }
    return info;
  }
}
