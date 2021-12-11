package org.jetbrains.research.refactorinsight.java.api.util;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.decomposition.AbstractStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;

import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.research.refactorinsight.common.utils.StringUtils.calculateSignatureWithoutClassName;

public class Utils {

    public static String calculateSignatureForJavaMethod(UMLOperation operation) {
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

    public static CodeRange createCodeRangeFromJava(gr.uom.java.xmi.diff.CodeRange codeRange) {
        return new CodeRange(codeRange.getStartLine(), codeRange.getEndLine(),
                codeRange.getStartColumn(), codeRange.getEndColumn(),
                codeRange.getFilePath());
    }

    public static LocationInfo createLocationInfoFromJava(gr.uom.java.xmi.LocationInfo locationInfo) {
        return new LocationInfo(locationInfo.getFilePath(), locationInfo.getStartOffset(),
                locationInfo.getEndOffset(), locationInfo.getStartLine(),
                locationInfo.getEndLine(), locationInfo.getStartColumn(),
                locationInfo.getEndColumn(), locationInfo.getLength());
    }

    public static boolean isStatementsEqualJava(@NotNull List<AbstractStatement> statementsBefore,
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
