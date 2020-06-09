package ui.tree.renderer;

import data.Group;
import data.RefactoringInfo;
import java.util.HashMap;
import javax.swing.Icon;
import ui.tree.renderer.handlers.AbstractClassHandler;
import ui.tree.renderer.handlers.AttributeHandler;
import ui.tree.renderer.handlers.ClassHandler;
import ui.tree.renderer.handlers.InterfaceHandler;
import ui.tree.renderer.handlers.MethodHandler;
import ui.tree.renderer.handlers.PackageHandler;
import ui.tree.renderer.handlers.VariableHandler;

public class CellIconFactory {

  HashMap<Group, IconHandler> map = new HashMap<>();

  /**
   * Fills map with all types of handlers.
   */
  public CellIconFactory() {
    map.put(Group.PACKAGE, new PackageHandler());
    map.put(Group.ATTRIBUTE, new AttributeHandler());
    map.put(Group.VARIABLE, new VariableHandler());
    map.put(Group.ABSTRACT, new AbstractClassHandler());
    map.put(Group.INTERFACE, new InterfaceHandler());
    map.put(Group.METHOD, new MethodHandler());
    map.put(Group.CLASS, new ClassHandler());
  }

  Icon create(RefactoringInfo info, boolean leaf, boolean path) {
    return map.get(info.getGroup()).getIcon(info, leaf, path);
  }
}
