package ui.tree.renderers;

import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.Node;

public interface IconHandler {

  Icon getIcon(RefactoringInfo info, Node node);

}
