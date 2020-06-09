package ui.tree.renderer;

import data.RefactoringInfo;
import javax.swing.Icon;

public interface IconHandler {

  Icon getIcon(RefactoringInfo info, boolean leaf, boolean path);

}
