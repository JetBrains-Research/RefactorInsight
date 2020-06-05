package utils;

import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import java.util.ArrayList;

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
}
