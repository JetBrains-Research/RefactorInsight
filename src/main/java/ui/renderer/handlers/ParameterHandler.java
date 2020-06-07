package ui.renderer.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.renderer.IconHandler;

public class ParameterHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, boolean leaf, boolean path) {
    if (!leaf) {
      return AllIcons.Nodes.Method;
    }
    return AllIcons.Nodes.Parameter;
  }
}