package ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.Node;
import ui.tree.renderers.IconHandler;

public class PackageHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    return AllIcons.Nodes.Package;
  }
}