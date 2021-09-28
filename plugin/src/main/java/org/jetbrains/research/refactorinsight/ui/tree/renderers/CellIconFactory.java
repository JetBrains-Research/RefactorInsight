package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import java.util.EnumMap;
import java.util.Map;
import javax.swing.Icon;
import com.intellij.icons.AllIcons;
import icons.RefactorInsightIcons;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.DisplayedGroup;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.AbstractClassHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.AttributeHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.ClassHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.InterfaceHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.MethodHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.PackageHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.VariableHandler;

public class CellIconFactory {
  private final Map<Group, IconHandler> map = new EnumMap<>(Group.class);

  /**
   * Fills map with all types of handlers.
   */
  public CellIconFactory() {
    map.put(Group.ATTRIBUTE, new AttributeHandler());
    map.put(Group.ABSTRACT, new AbstractClassHandler());
    map.put(Group.INTERFACE, new InterfaceHandler());
    map.put(Group.METHOD, new MethodHandler());
    map.put(Group.CLASS, new ClassHandler());
    map.put(Group.PACKAGE, new PackageHandler());
    map.put(Group.VARIABLE, new VariableHandler());
  }

  /**
   * Get icon from node type and info.
   */
  public Icon create(RefactoringInfo info, Node node) {
    switch (node.getType()) {
      case GROUP:
        return groupIcon(info);
      case TYPE:
        return RefactorInsightIcons.node;
      default:
        return map.get(info.getGroup()).getIcon(info, node);
    }
  }

  private Icon groupIcon(RefactoringInfo info) {
    switch (DisplayedGroup.fromInternalGroup(info.getGroup())) {
      case METHOD:
        return AllIcons.Nodes.Method;
      case CLASS:
        return AllIcons.Nodes.Class;
      case VARIABLE:
        return AllIcons.Nodes.Variable;
      case PACKAGE:
        return AllIcons.Nodes.Package;
      default:
        throw new IllegalStateException("Unexpected value: "
            + DisplayedGroup.fromInternalGroup(info.getGroup()));
    }
  }
}
