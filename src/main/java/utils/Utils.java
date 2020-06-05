package utils;

import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.text.JBDateFormat;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import data.RefactoringInfo;
import gr.uom.java.xmi.UMLOperation;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class Utils {

  /**
   * Used for storing and disposing the MainVcsLogs used for method history action.
   */
  private static ArrayList<MainVcsLogUi> logs = new ArrayList<>();

  /**
   * Expands all the nodes of the tree.
   *
   * @param tree          to be expanded.
   * @param startingIndex the starting index for expansion.
   * @param rowCount      of the tree.
   */
  public static void expandAllNodes(Tree tree, int startingIndex, int rowCount) {
    for (int i = startingIndex; i < rowCount; ++i) {
      tree.expandRow(i);
    }
    if (tree.getRowCount() != rowCount) {
      expandAllNodes(tree, rowCount, tree.getRowCount());
    }
  }

  /**
   * Method used for a presentable displaying of class change.
   *
   * @param nameBefore class name before.
   * @param nameAfter  class name after.
   * @return the index where the string are different.
   */
  public static int indexOfDifference(String nameBefore, String nameAfter) {
    int minLen = Math.min(nameBefore.length(), nameAfter.length());
    int last = 0;
    for (int i = 0; i != minLen; i++) {
      char chA = nameBefore.charAt(i);
      char chB = nameAfter.charAt(i);
      if (nameBefore.charAt(i) == '.' && nameAfter.charAt(i) == '.') {
        last = i + 1;
      }
      if (chA != chB) {
        return last;
      }
    }
    return last;
  }

  /**
   * Method used for disposing the logs that were created and shown for the method history action.
   * Called when the project is closing.
   * Avoids memory leaks.
   */
  public static void dispose() {
    for (MainVcsLogUi log : logs) {
      Disposer.dispose(log);
    }
  }

  /**
   * Adds a MainVcsLogUI to the list.
   *
   * @param log to add.
   */
  public static void add(MainVcsLogUi log) {
    logs.add(log);
  }

  /**
   * Calculates the signature of a PsiMethod such that it matches the once calculated
   * for a RefactoringMiner UMLOperation.
   *
   * @param method to calcukate signature for.
   * @return the signature.
   */
  public static String calculateSignature(PsiMethod method) {
    String signature = method.getName();
    signature = method.getContainingClass().getQualifiedName() + "." + signature + "(";
    PsiParameterList parameterList = method.getParameterList();
    int parametersCount = parameterList.getParametersCount();

    for (int i = 0; i < parametersCount; i++) {
      if (i != parametersCount - 1) {
        signature += parameterList.getParameter(i).getType().getPresentableText() + ",";
      } else {
        signature += parameterList.getParameter(i).getType().getPresentableText();
      }
    }
    signature += ")";
    return signature;
  }

  /**
   * Builder for a method's signature.
   *
   * @param operation retrieved from RefactoringMiner
   * @return a String signature of the operation.
   */
  public static String calculateSignature(UMLOperation operation) {
    StringBuilder builder = new StringBuilder();
    builder.append(operation.getClassName())
        .append(".")
        .append(operation.getName())
        .append("(");
    operation.getParameterTypeList()
        .forEach(x -> builder.append(x).append(", "));

    if (operation.getParameterTypeList().size() > 0) {
      builder.deleteCharAt(builder.length() - 1);
      builder.deleteCharAt(builder.length() - 1);
    }

    builder.append(")");
    return builder.toString();
  }

  /**
   * Creates presentable text for a refactoring info.
   * Displayed in Method History Toolbar.
   *
   * @param info to create presentable Text for.
   * @return
   */
  @NotNull
  public static String getTextMethodToolbar(RefactoringInfo info) {
    final String second = JBDateFormat.getFormatter()
        .formatPrettyDateTime(info.getTimestamp());
    final String first = info.getName();
    return createHtml(second, first);
  }

  @NotNull
  private static String createHtml(String second, String first) {
    final String str = getStringForRefactoringNode(first, second);
    return createHtml(str);
  }

  /**
   * Creates presentable text.
   *
   * @param str html
   * @return html representation of the text
   */
  @NotNull
  public static String createHtml(String str) {
    StringBuffer html = new StringBuffer(str);
    return html.toString();
  }

  @NotNull
  private static String getStringForRefactoringNode(String first, String second) {
    return "<html> <b>" + first + "</b> <font color=\"#696969\"> "
        + second + "</font></html>";
  }

  /**
   * Creates presentable text for leaf nodes.
   *
   * @param first  line number (after refactoring)
   * @param second element
   * @param third  filename
   * @return text
   */
  public static String getStringLeaf(String first, String second, String third) {
    return "<html> <font color=\"#696969\"> " + first + " </font> "
        + second + (third.equals("") ? "</html>"
        : ("<font color=\"#696969\"> in file " + third + " </font></html>"));
  }


  /**
   * Creates presentable text for the VcsLogUI refactoring nodes.
   *
   * @param info to create text for.
   * @return presentable text.
   */
  @NotNull
  public static String getTextLogUI(RefactoringInfo info) {
    String second =
        info.getIncludingRefactorings().size() > 0
            ? info.getIncludingRefactorings().toString() : "  ";
    second = second.substring(1, second.length() - 1);
    second = second.equals("") ? "" : (" implied " + second);
    final String first = info.getName();
    return createHtml(second, first);
  }

  /**
   * Finds the start and ending column of a word in a text.
   *
   * @param text Java Code
   * @param word Word to look for.
   * @param line In what line the word can be found.
   * @return Start and ending column in an int[]
   */
  public static int[] findColumns(String text, String word, int line) {
    String[] lines = text.split("\r\n|\r|\n");
    int startColumn = lines[line - 1].indexOf(word) + 1;
    int endColumn = startColumn + word.length();
    return new int[] {startColumn, endColumn};
  }

  /**
   * Calculates offset.
   *
   * @param text   to search in
   * @param line   line
   * @param column column
   * @return offset
   */
  public static int getOffset(String text, int line, int column) {
    int offset = 0;
    String[] lines = text.split("\r\n|\r|\n");
    for (int i = 0; i < line - 1; i++) {
      offset += lines[i].length() + 1;
    }
    return offset + column - 1;
  }

  /**
   * Returns last line count.
   *
   * @param text to search in
   * @return length of the text
   */
  public static int getMaxLine(String text) {
    return text.split("\r\n|\r|\n").length;
  }
}
