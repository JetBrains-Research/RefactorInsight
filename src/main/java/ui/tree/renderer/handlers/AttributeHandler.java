package ui.tree.renderer.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.renderer.IconHandler;

public class AttributeHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, boolean leaf, boolean path) {
    if (!leaf && path) {
      return AllIcons.Nodes.Class;
    }
    if (leaf && info.getName().contains("Annotation")) {
      return AllIcons.Nodes.Annotationtype;
    }
    return AllIcons.Nodes.Field;
  }
}