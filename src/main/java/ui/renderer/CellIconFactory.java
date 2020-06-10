package ui.renderer;

import data.RefactoringInfo;
import java.util.HashMap;
import javax.swing.Icon;
import ui.renderer.handlers.AbstractClassHandler;
import ui.renderer.handlers.AttributeHandler;
import ui.renderer.handlers.ClassHandler;
import ui.renderer.handlers.InterfaceHandler;
import ui.renderer.handlers.MethodHandler;
import ui.renderer.handlers.PackageHandler;
import ui.renderer.handlers.VariableHandler;

public class CellIconFactory {

  HashMap<RefactoringInfo.Group, IconHandler> map = new HashMap<>();

  /**
   * Fills map with all types of handlers.
   */
  public CellIconFactory() {
    map.put(RefactoringInfo.Group.PACKAGE, new PackageHandler());
    map.put(RefactoringInfo.Group.ATTRIBUTE, new AttributeHandler());
    map.put(RefactoringInfo.Group.VARIABLE, new VariableHandler());
    map.put(RefactoringInfo.Group.ABSTRACT, new AbstractClassHandler());
    map.put(RefactoringInfo.Group.INTERFACE, new InterfaceHandler());
    map.put(RefactoringInfo.Group.METHOD, new MethodHandler());
    map.put(RefactoringInfo.Group.CLASS, new ClassHandler());
  }

  Icon create(RefactoringInfo info, boolean leaf, boolean path) {
    return map.get(info.getGroup()).getIcon(info, leaf, path);
  }
}
