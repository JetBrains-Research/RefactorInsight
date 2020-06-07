package ui.renderer.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.renderer.IconHandler;

public class PackageHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, boolean leaf, boolean path) {
    return AllIcons.Nodes.Package;
  }
}