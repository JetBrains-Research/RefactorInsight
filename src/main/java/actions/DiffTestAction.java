package actions;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffTool;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.impl.DiffWindow;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.simple.ThreesideTextDiffViewerEx;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DiffTestAction extends AnAction {
  @SuppressWarnings("checkstyle:OperatorWrap")
  String beforeString = "package actions;\n" +
      "\n" +
      "public class Joe {\n" +
      "  \n" +
      "  void method() {\n" +
      "    System.out.println(\"A\");\n" +
      "    System.out.println(\"B\");\n" +
      "    System.out.println(\"C\");\n" +
      "    System.out.println(\"D\");\n" +
      "    System.out.println(\"E\");\n" +
      "    System.out.println(\"F\");\n" +
      "    System.out.println(\"G\");\n" +
      "  }\n" +
      "}\n";

  @SuppressWarnings("checkstyle:OperatorWrap")
  String afterString = "package actions;\n" +
      "\n" +
      "public class Joe {\n" +
      "  \n" +
      "  void method() {\n" +
      "    System.out.println(\"A\");\n" +
      "    newMethod();\n" +
      "    System.out.println(\"G\");\n" +
      "  }\n" +
      "  \n" +
      "  void newMethod() {\n" +
      "    System.out.println(\"B\");\n" +
      "    System.out.println(\"C\");\n" +
      "    System.out.println(\"D\");\n" +
      "    System.out.println(\"E\");\n" +
      "    System.out.println(\"F\");\n" +
      "  }\n" +
      "  \n" +
      "}\n";

  @SuppressWarnings("checkstyle:OperatorWrap")
  String afterString2 = "package actions;\n" +
      "\n" +
      "public class Joe {\n" +
      "  \n" +
      "  void method() {\n" +
      "    System.out.println(\"A\");\n" +
      "    newMethod();\n" +
      "    System.out.println(\"G\");\n" +
      "  }\n" +
      "  \n" +
      "  void newMethod() {\n" +
      "    System.out.println(\"F\");\n" +
      "  }\n" +
      "  \n" +
      "}\n";

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {

    DiffContentFactoryEx factoryEx = DiffContentFactoryEx.getInstanceEx();

    DiffContent beforeContent =
        factoryEx.create(e.getProject(), beforeString, JavaClassFileType.INSTANCE);
    DiffContent afterContent =
        factoryEx.create(e.getProject(), afterString, JavaClassFileType.INSTANCE);
    DiffContent third =
        factoryEx.create(e.getProject(), afterString2, JavaClassFileType.INSTANCE);

    DiffRequest request =
        new SimpleDiffRequest("Refactorings", beforeContent, afterContent, null, null);

    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
        (text1, text2, policy, innerChanges, indicator) -> {
          return getFrags();
        });

    ThreesideTextDiffViewerEx merp;

    DiffManager.getInstance().showDiff(e.getProject(), request);
  }

  public List<LineFragment> getFrags() {
    List<LineFragment> frags = new ArrayList<>();
    LineFragmentImpl frag1 = new LineFragmentImpl(6, 11, 11, 16, 0, 0, 0, 0);
    LineFragmentImpl frag2 = new LineFragmentImpl(12, 12, 6, 7, 0, 0, 0, 0);
    frags.add(frag1);
    frags.add(frag2);
    return frags;
  }


}
