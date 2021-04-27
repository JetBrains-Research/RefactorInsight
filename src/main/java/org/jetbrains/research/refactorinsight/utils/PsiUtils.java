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
import java.util.Optional;

public class PsiUtils {
  private PsiUtils() {
  }

  /**
   * Find PsiClass in PsiFile by qualified name.
   */
  @Nullable
  public static PsiClass findClass(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    if (psiFile.getLanguage().is(JavaLanguage.INSTANCE)) {
      return findClassJava(psiFile, qualifiedName);
    } else {
      return null;
    }
  }

  /**
   * Find PsiMethod in PsiFile by qualified name.
   */
  @Nullable
  public static PsiMethod findMethod(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    if (psiFile.getLanguage().is(JavaLanguage.INSTANCE)) {
      return findMethodJava(psiFile, qualifiedName);
    } else {
      return null;
    }
  }

  /**
   * Find PsiClass in Java PsiFile by qualified name.
   */
  @Nullable
  public static PsiClass findClassJava(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiElementProcessor.FindElement<PsiElement> processor = new PsiElementProcessor.FindElement<>() {
      @Override
      public boolean execute(@NotNull PsiElement each) {
        if (each instanceof PsiClass) {
          String eachQualifiedName = ((PsiClass) each).getQualifiedName();
          if (qualifiedName.equals(eachQualifiedName)) {
            return setFound(each);
          }
        }
        return true;
      }
    };
    PsiTreeUtil.processElements(psiFile, processor);
    return (PsiClass) processor.getFoundElement();
  }

  /**
   * Find PsiMethod in Java PsiFile by qualified name.
   */
  @Nullable
  public static PsiMethod findMethodJava(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiClass psiClass = findClassJava(psiFile, methodClassName(qualifiedName));
    if (psiClass == null) {
      return null;
    }
    PsiMethod[] psiMethods = psiClass.findMethodsByName(methodSimpleName(qualifiedName), false);
    if (psiMethods.length == 0) {
      throw new AssertionError("Can't find method by name");
    }
    String[] searchedMethodSignature = methodSignature(qualifiedName);
    Optional<PsiMethod> foundMethod = Arrays.stream(psiMethods)
        .filter(psiMethod -> Arrays.equals(searchedMethodSignature, methodSignature(psiMethod)))
        .findAny();
    if (foundMethod.isEmpty()) {
      throw new AssertionError("Can't find method by type");
    }
    return foundMethod.get();
  }

  @NotNull
  private static String[] methodSignature(@NotNull String qualifiedName) {
    int parametersListStart = qualifiedName.indexOf('(');
    int parametersListEnd = qualifiedName.length() - 1;
    return parametersListStart + 1 < parametersListEnd
        ? qualifiedName.substring(parametersListStart + 1, parametersListEnd).split(", ")
        : new String[]{};
  }

  @NotNull
  private static String[] methodSignature(@NotNull PsiMethod psiMethod) {
    return Arrays.stream(psiMethod.getParameterList().getParameters())
        .map(PsiParameter::getType)
        .map(PsiType::getPresentableText)
        .toArray(String[]::new);
  }

  @NotNull
  private static String methodClassName(@NotNull String qualifiedName) {
    return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
  }

  @NotNull
  private static String methodSimpleName(@NotNull String qualifiedName) {
    return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.indexOf('('));
  }
}
