package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

//TODO: The test broke after updating to 2020.3. Investigate the ways to fix it.

/*
public class ActionTest extends BasePlatformTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();

  }

  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  protected String getTestDataPath() {
    return "src/test/testData/example-refactorings";
  }

  public void testUpdate() {
    myFixture.configureByFile("src/main/java/vehicles/cars/Car.java");
    AnAction action = new RefactoringHistoryAction();
    Presentation presentation = myFixture.testAction(action);
    assertFalse(presentation.isEnabled());
    assertFalse(presentation.isVisible());
  }
}*/
