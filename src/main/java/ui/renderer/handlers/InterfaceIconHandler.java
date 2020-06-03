package ui.renderer.handlers;

import com.intellij.icons.AllIcons;
import javax.swing.Icon;
import ui.renderer.CellIconHandler;

public class InterfaceIconHandler implements CellIconHandler {

  @Override
  public Icon handle() {
    return AllIcons.Nodes.Interface;
  }
}
