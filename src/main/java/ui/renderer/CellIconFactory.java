package ui.renderer;

import com.intellij.icons.AllIcons;
import data.Group;
import data.RefactoringInfo;
import java.util.HashMap;
import javax.swing.Icon;

public class CellIconFactory {

  HashMap<Group, Icon> map = new HashMap<>();

  /**
   * Fills map with all types of handlers.
   */
  public CellIconFactory() {
    map.put(Group.PACKAGE, AllIcons.Nodes.Package);
    map.put(Group.ATTRIBUTE, AllIcons.Nodes.Field);
    map.put(Group.VARIABLE, AllIcons.Nodes.Variable);
    map.put(Group.PARAMETER, AllIcons.Nodes.Parameter);
    map.put(Group.ABSTRACT, AllIcons.Nodes.AbstractClass);
    map.put(Group.INTERFACE, AllIcons.Nodes.Interface);
    map.put(Group.METHOD, AllIcons.Nodes.Method);
    map.put(Group.CLASS, AllIcons.Nodes.Class);
  }

  Icon create(RefactoringInfo info) {
    return map.get(info.getGroup());
  }
}
