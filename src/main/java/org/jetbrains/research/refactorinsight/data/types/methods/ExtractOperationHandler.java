package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.jetbrains.research.kotlinrminer.uml.UMLType;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.folding.FoldingPositions;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class ExtractOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;

    info.setFoldingPositionsMid(FoldingPositions.fromMethod(ref.getExtractedOperation()));

    String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
    String classNameAfter = ref.getExtractedOperation().getClassName();
    List<String> parameterTypeList = new ArrayList<>();
    for (gr.uom.java.xmi.UMLType type : ref.getExtractedOperation().getParameterTypeList()) {
      parameterTypeList.add(type.toString());
    }

    String extractedMethod = StringUtils
        .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

    if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      info.setGroup(Group.METHOD)
          .setThreeSided(true)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(ref.getExtractedCodeRangeToExtractedOperation()),
              new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              RefactoringLine.VisualisationType.LEFT,
              null,
              RefactoringLine.MarkingOption.NONE,
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              new CodeRange(ref.getSourceOperationCodeRangeBeforeExtraction()),
              new CodeRange(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
              new CodeRange(invocation),
              RefactoringLine.VisualisationType.RIGHT,
              refactoringLine -> refactoringLine.setWord(new String[]{
                  null,
                  ref.getExtractedOperation().getName(),
                  null
              }),
              RefactoringLine.MarkingOption.EXTRACT,
              true));
      return info;
    } else {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(new CodeRange(ref.getSourceOperationCodeRangeBeforeExtraction()),
              new CodeRange(ref.getSourceOperationCodeRangeAfterExtraction()),
              false)
          .addMarking(new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(ref.getExtractedCodeRangeToExtractedOperation()),
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(invocation),
              null,
              RefactoringLine.MarkingOption.ADD,
              true)
      );
      return info;
    }
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractOperationRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractOperationRefactoring) refactoring;

    info.setFoldingPositionsMid(FoldingPositions.fromMethod(ref.getExtractedOperation()));

    String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
    String classNameAfter = ref.getExtractedOperation().getClassName();
    List<String> parameterTypeList = new ArrayList<>();
    for (UMLType type : ref.getExtractedOperation().getParameterTypeList()) {
      parameterTypeList.add(type.toString());
    }

    String extractedMethod = StringUtils
        .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

    if (ref.getRefactoringType()
        == org.jetbrains.research.kotlinrminer.api.RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      info.setGroup(Group.METHOD)
          .setThreeSided(true)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(ref.getExtractedCodeRangeToExtractedOperation()),
              new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              RefactoringLine.VisualisationType.LEFT,
              null,
              RefactoringLine.MarkingOption.NONE,
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              new CodeRange(ref.getSourceOperationCodeRangeBeforeExtraction()),
              new CodeRange(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
              new CodeRange(invocation),
              RefactoringLine.VisualisationType.RIGHT,
              refactoringLine -> refactoringLine.setWord(new String[]{
                  null,
                  ref.getExtractedOperation().getName(),
                  null
              }),
              RefactoringLine.MarkingOption.EXTRACT,
              true));
    } else {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(new CodeRange(ref.getSourceOperationCodeRangeBeforeExtraction()),
              new CodeRange(ref.getSourceOperationCodeRangeAfterExtraction()),
              false)
          .addMarking(new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(ref.getExtractedCodeRangeToExtractedOperation()),
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              new CodeRange(ref.getExtractedCodeRangeFromSourceOperation()),
              new CodeRange(invocation),
              null,
              RefactoringLine.MarkingOption.ADD,
              true)
      );
    }
    return info;
  }
}
