package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import javax.swing.Icon;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class PackageHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    return AllIcons.Nodes.Package;
  }
}