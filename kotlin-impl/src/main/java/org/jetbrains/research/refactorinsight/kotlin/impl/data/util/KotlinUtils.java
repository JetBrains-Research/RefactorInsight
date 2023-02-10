package org.jetbrains.research.refactorinsight.kotlin.impl.data.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.kotlinrminer.ide.decomposition.AbstractStatement;
import org.jetbrains.research.kotlinrminer.ide.uml.UMLOperation;
import org.jetbrains.research.kotlinrminer.ide.uml.UMLType;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.research.refactorinsight.utils.StringUtils.calculateSignatureWithoutClassName;

public class KotlinUtils {

    public static String calculateSignatureForKotlinMethod(UMLOperation operation) {
        StringBuilder builder = new StringBuilder();
        List<String> parameterTypeList = new ArrayList<>();
        for (UMLType type : operation.getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        builder.append(operation.getClassName())
                .append(".")
                .append(calculateSignatureWithoutClassName(operation.getName(), parameterTypeList));
        return builder.toString();
    }

    public static CodeRange createCodeRangeFromKotlin(org.jetbrains.research.kotlinrminer.ide.diff.CodeRange codeRange, RefactoringInfo info) {
        final String filePath = codeRange.getFilePath().startsWith(info.getProjectPath()) ?
                codeRange.getFilePath().substring(info.getProjectPath().length()) :
                codeRange.getFilePath();
        return new CodeRange(codeRange.getStartLine(), codeRange.getEndLine(),
                codeRange.getStartColumn(), codeRange.getEndColumn(),
                filePath);
    }

    public static LocationInfo createLocationInfoFromKotlin(org.jetbrains.research.kotlinrminer.ide.decomposition.LocationInfo locationInfo) {
        return new LocationInfo(locationInfo.getFilePath(), locationInfo.getStartOffset(),
                locationInfo.getEndOffset(), locationInfo.getStartLine(),
                locationInfo.getEndLine(), locationInfo.getStartColumn(),
                locationInfo.getEndColumn(), locationInfo.getLength());
    }

    public static boolean isStatementsEqualKotlin(@NotNull List<AbstractStatement> statementsBefore,
                                                  @NotNull List<AbstractStatement> statementsAfter) {
        if (statementsBefore.size() == statementsAfter.size()) {
            boolean equal = true;
            for (int i = 0; i < statementsBefore.size() && equal; i++) {
                equal = statementsBefore.get(i).equalFragment(statementsAfter.get(i));
            }
            return equal;
        } else {
            return false;
        }
    }

}
