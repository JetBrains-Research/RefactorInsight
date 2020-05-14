package data.types;

import data.MethodRefactoringData;
import org.refactoringminer.api.Refactoring;
import data.RefactoringInfo;
import data.TrueCodeRange;

import java.util.List;
import java.util.stream.Collectors;
import processors.MethodRefactoringProcessor;

public interface Handler {

  default RefactoringInfo handle(Refactoring refactoring, String commitId) {
    List<TrueCodeRange> left = refactoring.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());
    List<TrueCodeRange> right = refactoring.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());

    MethodRefactoringProcessor processor = new MethodRefactoringProcessor("");
    MethodRefactoringData ref = processor.process(refactoring);


    String signatureBefore = ref == null ? "" : ref.getMethodBefore();
    String signatureAfter = ref == null ? "" : ref.getMethodAfter();

    return new RefactoringInfo()
        .setName(refactoring.getName())
        .setText(refactoring.toString())
        .setType(refactoring.getRefactoringType())
        .setLeftSide(left)
        .setRightSide(right)
        .setCommitId(commitId)
        .setSignatureBefore(signatureBefore)
        .setSignatureAfter(signatureAfter);

  }

}
