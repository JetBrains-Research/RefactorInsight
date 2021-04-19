package org.jetbrains.research.refactorinsight.folding;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class JavaFileFinder {
  // Suppresses default constructor, ensuring non-instantiability.
  private JavaFileFinder() {
  }

  @Nullable
  public static PsiMethod findMethod(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    int classQualifiedNameEnd = qualifiedName.lastIndexOf('.');
    int parametersListStart = qualifiedName.indexOf('(');
    int parametersListEnd = qualifiedName.indexOf(')');
    assert parametersListEnd + 1 == qualifiedName.length();

    PsiClass psiClass = findClass(psiFile, qualifiedName.substring(0, classQualifiedNameEnd));
    if (psiClass != null) {
      PsiMethod[] psiMethods = psiClass.findMethodsByName(
          qualifiedName.substring(classQualifiedNameEnd + 1, parametersListStart), false);
      if (psiMethods.length > 0) {
        String[] searchedMethodParameters =
            parametersListStart + 1 < parametersListEnd
                ? qualifiedName.substring(parametersListStart + 1, parametersListEnd).split(", ")
                : new String[]{};
        for (PsiMethod psiMethod : psiMethods) {
          String[] methodParameters =
              Arrays.stream(psiMethod.getParameterList().getParameters())
                  .map(PsiParameter::getType)
                  .map(PsiType::getPresentableText)
                  .toArray(String[]::new);
          if (Arrays.equals(methodParameters, searchedMethodParameters)) {
            return psiMethod;
          }
        }
        throw new AssertionError("Can't find method by type");
      } else {
        throw new AssertionError("Can't find method by name");
      }
    }
    return null;
  }

  @Nullable
  public static PsiField findField(@NotNull PsiFile psiFile, @NotNull String classQualifiedName, @NotNull String fieldName) {
    PsiClass psiClass = findClass(psiFile, classQualifiedName);
    if (psiClass != null) {
      return psiClass.findFieldByName(fieldName, false);
    }
    return null;
  }

  @Nullable
  public static PsiClass findClass(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiElement[] children = psiFile.getChildren();
    for (PsiElement element : children) {
      if (element instanceof PsiClass) {
        PsiClass psiClass = (PsiClass) element;
        String className = psiClass.getQualifiedName();
        if (qualifiedName.startsWith(className)) {
          if (qualifiedName.equals(className)) {
            return psiClass;
          }
          String[] path = qualifiedName.substring(className.length() + 1).split("\\.");
          for (String subclass : path) {
            psiClass = psiClass.findInnerClassByName(subclass, false);
            if (psiClass == null) {
              throw new AssertionError("Can't find subclass");
            }
          }
          return psiClass;
        }
      }
    }
    return null;
  }
}
