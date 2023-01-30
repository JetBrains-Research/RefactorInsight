package org.jetbrains.research.refactorinsight.utils;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

public class SignatureUtils {

    /**
     * Calculates the signature of a PsiMethod such that it matches the once calculated
     * for a RefactoringMiner UMLOperation.
     *
     * @param method to calcukate signature for.
     * @return the signature.
     */
    public static String calculateSignature(PsiMethod method) {
        StringBuilder signature = new StringBuilder(method.getName());
        signature = new StringBuilder(method.getContainingClass().getQualifiedName() + "." + signature + "(");
        PsiParameterList parameterList = method.getParameterList();
        int parametersCount = parameterList.getParametersCount();

        for (int i = 0; i < parametersCount; i++) {
            if (i != parametersCount - 1) {
                signature.append(parameterList.getParameter(i).getType().getPresentableText()).append(", ");
            } else {
                signature.append(parameterList.getParameter(i).getType().getPresentableText());
            }
        }
        signature.append(")");
        return signature.toString();
    }


    /**
     * Computes the signature of a PsiField in order to match a RefactoringMiner attribute.
     *
     * @param field to compute signature for.
     * @return signature of a field.
     */
    public static String getFieldSignature(PsiField field) {
        return field.getContainingClass().getQualifiedName()
                + "|" + field.getName() + " : " + field.getType().getPresentableText();
    }

    /**
     * Method for create a presentable String out of the
     * name refactoring.
     *
     * @return presentable String that shows the changes if existent, else shows a presentable name.
     */
    public static String getDisplayableName(RefactoringInfo info) {
        if (info.getGroup() == Group.PACKAGE) {
            if (info.getNameBefore().equals(info.getNameAfter())) {
                return info.getNameBefore();
            } else {
                return info.getNameBefore() + " -> " + info.getNameAfter();
            }
        }
        String before = info.getNameBefore();
        String after = info.getNameAfter();

        if (info.getGroup() == Group.METHOD || info.getGroup() == Group.VARIABLE) {
            String paramsBefore = before.substring(before.lastIndexOf("("));
            String paramsAfter = after.substring(after.lastIndexOf("("));
            before = before.substring(0, before.lastIndexOf("("));
            after = after.substring(0, after.lastIndexOf("("));
            before = before.substring(before.lastIndexOf(".") + 1);
            after = after.substring(after.lastIndexOf(".") + 1);
            before = before + paramsBefore;
            after = after + paramsAfter;
        } else if (info.getGroup() != Group.ATTRIBUTE) {
            if (before.contains(".")) {
                before = info.getNameBefore().substring(info.getNameBefore().lastIndexOf(".") + 1);
            }
            if (after.contains(".")) {
                after = info.getNameAfter().substring(info.getNameAfter().lastIndexOf(".") + 1);
            }
        }
        if (before.equals(after)) {
            return before;
        } else {
            return before + " -> " + after;
        }
    }
}
