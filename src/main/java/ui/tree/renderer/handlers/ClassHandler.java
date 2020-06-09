package ui.tree.renderer.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.renderer.IconHandler;

public class ClassHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, boolean leaf, boolean path) {
    if (!leaf && path) {
      return AllIcons.Nodes.Package;
    }
    if (leaf && info.getName().contains("Annotation")) {
      return AllIcons.Nodes.Annotationtype;
    }
    return AllIcons.Nodes.Class;
  }
}