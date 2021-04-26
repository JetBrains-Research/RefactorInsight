package org.jetbrains.research.refactorinsight.utils;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PsiUtils {
  // Suppresses default constructor, ensuring non-instantiability.
  private PsiUtils() {
  }

  @Nullable
  public static PsiMethod findMethod(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    if (psiFile.getLanguage().is(JavaLanguage.INSTANCE)) {
      return findMethodJava(psiFile, qualifiedName);
    } else {
      return null;
    }
  }

  @Nullable
  public static PsiMethod findMethodJava(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    int classQualifiedNameEnd = qualifiedName.lastIndexOf('.');
    int parametersListStart = qualifiedName.indexOf('(');
    int parametersListEnd = qualifiedName.indexOf(')');
    assert parametersListEnd + 1 == qualifiedName.length();

    PsiClass psiClass = findClassJava(psiFile, qualifiedName.substring(0, classQualifiedNameEnd));
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
  public static PsiClass findClass(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    if (psiFile.getLanguage().is(JavaLanguage.INSTANCE)) {
      return findClassJava(psiFile, qualifiedName);
    } else {
      return null;
    }
  }

  @Nullable
  public static PsiClass findClassJava(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiElementProcessor.FindElement<PsiElement> processor = new PsiElementProcessor.FindElement<>() {
      @Override
      public boolean execute(@NotNull PsiElement each) {
        if (each instanceof PsiClass) {
          String eachQualifiedName = ((PsiClass) each).getQualifiedName();
          if (qualifiedName.equals(eachQualifiedName)) {
            return setFound(each);
          } else {
            return true;
          }
        } else {
          return true;
        }
      }
    };
    PsiTreeUtil.processElements(psiFile, processor);
    return (PsiClass) processor.getFoundElement();
  }
}
