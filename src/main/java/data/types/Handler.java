package data.types;

import com.intellij.openapi.project.Project;
import data.RefactoringEntry;
import data.RefactoringInfo;
import gr.uom.java.xmi.UMLOperation;
import org.refactoringminer.api.Refactoring;

public abstract class Handler {

  /**
   * Finds the start and ending column of a word in a text.
   * @param text Java Code
   * @param word Word to look for.
   * @param line In what line the word can be found.
   * @return Start and ending column in an int[]
   */
  public int[] findColumns(String text, String word, int line) {
    String[] lines = text.split("\r\n|\r|\n");
    int startColumn = lines[line - 1].indexOf(word) + 1;
    int endColumn = startColumn + word.length();
    return new int[] {startColumn, endColumn};
  }

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from RefactoringMiner
   * @return RefactoringInfo
   */
  public RefactoringInfo handle(Refactoring refactoring, RefactoringEntry entry, Project project) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(refactoring.getRefactoringType())
        .setName(refactoring.getName())
        .setEntry(entry);

    return specify(refactoring, info, project);
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info,
                                          Project project);


}
