package ui.tree.renderers;

import data.Group;
import data.RefactoringInfo;
import java.util.HashMap;
import javax.swing.Icon;
import ui.tree.Node;
import ui.tree.renderers.handlers.AbstractClassHandler;
import ui.tree.renderers.handlers.AttributeHandler;
import ui.tree.renderers.handlers.ClassHandler;
import ui.tree.renderers.handlers.InterfaceHandler;
import ui.tree.renderers.handlers.MethodHandler;
import ui.tree.renderers.handlers.PackageHandler;
import ui.tree.renderers.handlers.VariableHandler;


public class CellIconFactory {
  HashMap<Group, IconHandler> map = new HashMap<>();

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

  public Icon create(RefactoringInfo info, Node node) {
    return map.get(info.getGroup()).getIcon(info, node);
  }
}
