package org.jetbrains.research.refactorinsight.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import org.junit.Test;

public class UtilsTest {

  @Test
  public void indexOfDifferenceEquals() {
    assertEquals(8, StringUtils.indexOfDifference("src.com.test", "src.com.test"));
  }

  @Test
  public void indexOfDifferenceDifferentClasses() {
    assertEquals(4, StringUtils.indexOfDifference("com.test1", "com.test2"));
  }

  @Test
  public void indexOfDifferenceDifferentLengths() {
    assertEquals(4, StringUtils.indexOfDifference("com.test", "com.testNew"));
  }

  @Test
  public void indexOfDifference() {
    assertEquals(4, StringUtils.indexOfDifference("com.interface", "com.class"));
  }


  @Test
  public void indexOfDifferenceDifferentPackages() {
    assertEquals(0, StringUtils.indexOfDifference("src.interface", "src2.class"));
  }

  @Test
  public void calculateSignature() {
    PsiMethod psiMethod = mock(PsiMethod.class);
    PsiClass psiClass = mock(PsiClass.class);
    when(psiMethod.getContainingClass()).thenReturn(psiClass);
    when(psiClass.getQualifiedName()).thenReturn("testClass");
    when(psiMethod.getName()).thenReturn("testMethod");
    PsiParameterList parameterList = mock(PsiParameterList.class);
    PsiParameter parameter = mock(PsiParameter.class);
    when(psiMethod.getParameterList()).thenReturn(parameterList);
    when(parameterList.getParametersCount()).thenReturn(2);
    when(parameterList.getParameter(0)).thenReturn(parameter);
    when(parameterList.getParameter(1)).thenReturn(parameter);
    PsiType type = mock(PsiType.class);
    when(parameter.getType()).thenReturn(type);
    when(type.getPresentableText()).thenReturn("String");
    assertEquals("testClass.testMethod(String, String)", StringUtils.calculateSignature(psiMethod));
  }

  @Test
  public void sanitizerTest(){
    String unSanitized = "this/string/is#not_escaped";
    String sanitized = "this/string/is##not#_escaped";
    assertEquals(sanitized, StringUtils.sanitize(unSanitized));
    assertEquals(unSanitized, StringUtils.deSanitize(sanitized));
  }
}
