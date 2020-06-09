package ui.tree.renderer.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.renderer.IconHandler;

public class VariableHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, boolean leaf, boolean path) {
    if (!leaf) {
      return AllIcons.Nodes.Method;
    }
    return AllIcons.Nodes.Variable;
  }
}