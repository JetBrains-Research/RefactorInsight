package ui.renderer;

import data.Group;
import data.RefactoringInfo;
import java.util.HashMap;
import javax.swing.Icon;
import ui.renderer.handlers.AbstractClassIconHandler;
import ui.renderer.handlers.AttributeIconHandler;
import ui.renderer.handlers.ClassIconHandler;
import ui.renderer.handlers.InterfaceIconHandler;
import ui.renderer.handlers.MethodIconHandler;
import ui.renderer.handlers.PackageIconHandler;
import ui.renderer.handlers.ParameterIconHandler;
import ui.renderer.handlers.VariableIconHandler;

public class CellIconFactory {

  HashMap<Group, CellIconHandler> map = new HashMap<>();

  /**
   * Fills map with all types of handlers.
   */
  public CellIconFactory() {
    map.put(Group.PACKAGE, new PackageIconHandler());
    map.put(Group.ATTRIBUTE, new AttributeIconHandler());
    map.put(Group.VARIABLE, new VariableIconHandler());
    map.put(Group.PARAMETER, new ParameterIconHandler());
    map.put(Group.ABSTRACT, new AbstractClassIconHandler());
    map.put(Group.INTERFACE, new InterfaceIconHandler());
    map.put(Group.METHOD, new MethodIconHandler());
    map.put(Group.CLASS, new ClassIconHandler());
  }

  Icon create(RefactoringInfo info) {
    return map.get(info.getGroup()).handle();
  }
}
