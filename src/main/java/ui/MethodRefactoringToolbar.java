package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

public class MethodRefactoringToolbar {

    private ToolWindowManager toolWindowManager;
    private ToolWindow toolWindow;

    public MethodRefactoringToolbar(Project project) {
        toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindow = toolWindowManager
                .registerToolWindow("Refactoring History", true, ToolWindowAnchor.BOTTOM);
    }
}
