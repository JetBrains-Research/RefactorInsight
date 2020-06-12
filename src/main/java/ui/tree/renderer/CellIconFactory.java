package ui.tree.renderer;

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

  public Icon create(RefactoringInfo info, boolean leaf, boolean path) {
    return map.get(info.getGroup()).getIcon(info, leaf, path);
  }
}
