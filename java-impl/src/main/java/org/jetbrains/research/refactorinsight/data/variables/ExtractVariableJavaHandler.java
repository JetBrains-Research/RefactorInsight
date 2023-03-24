package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ExtractVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;

        info.addMarking(createCodeRangeFromJava(ref.leftSide().get(0)),
                createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()),
                null,
                RefactoringLine.MarkingOption.ADD,
                true);

        ref.getReferences().forEach(r ->
                info.addMarking(
                        createCodeRangeFromJava(r.getFragment1().codeRange()),
                        createCodeRangeFromJava(r.getFragment2().codeRange()),
                        refactoringLine -> refactoringLine.setWord(new String[]{
                                null,
                                null,
                                ref.getVariableDeclaration().getVariableName()
                        }),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true
                )
        );

        ref.getSubExpressionMappings().forEach(leafMapping ->
                info.addMarking(
                        createCodeRangeFromJava(leafMapping.getFragment1().codeRange()),
                        createCodeRangeFromJava(leafMapping.getFragment2().codeRange()),
                        true
                )
        );

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null);
    }

}
