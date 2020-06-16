package actions;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.TitlePanel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jetbrains.annotations.NotNull;

public class TestDiffAction extends AnAction {

  public String text = "/**\n" +
      " * Here some javadoge to test class coderanges pls dont fuck up miner..\n" +
      " */\n" +
      "public class Boat extends Vehicle {\n" +
      "\n" +
      "    private int[] wheels = new int[4];\n" +
      "    private String engine;\n" +
      "    private Mechanics mechanics;\n" +
      "\n" +
      "    public Boat(int wheel1, int wheel2, int wheel3, int wheel4) {\n" +
      "        super();\n" +
      "       wheels[0]  = wheel1;\n" +
      "       wheels[1]  = wheel2;\n" +
      "       wheels[2]  = wheel3;\n" +
      "       wheels[3]  = wheel4;\n" +
      "        engine = \"engine\";\n" +
      "        mechanics = new Mechanics();\n" +
      "    }\n" +
      "\n" +
      "    /**\n" +
      "     * Javadoge here for this awesome method!\n" +
      "     */\n" +
      "    public void drive() {\n" +
      "        System.out.println(engine);\n" +
      "    }\n" +
      "\n" +
      "    /**\n" +
      "     * Okay another javadoge...\n" +
      "     * @return Something arbitrair\n" +
      "     */\n" +
      "    public int doSomethingElse() {\n" +
      "        return wheels[0] + wheels[1] + wheels[2] + wheels[3];\n" +
      "    }\n" +
      "\n" +
      "\n" +
      "}\n";


  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Editor editor1 = EditorFactory.getInstance()
        .createEditor(new DocumentImpl(text), e.getProject(), JavaFileType.INSTANCE, true);
    EditorInfo e1 = new EditorInfo(editor1, 10);

    Editor editor2 = EditorFactory.getInstance()
        .createEditor(new DocumentImpl(text), e.getProject(), JavaFileType.INSTANCE, true);
    EditorInfo e2 = new EditorInfo(editor2, 15);


    JBList<EditorInfo> list = new JBList<>(JBList.createDefaultListModel(e1, e2));
    list.setCellRenderer(new MyRenderer());
    JBScrollPane pane = new JBScrollPane(list);

    ComponentPopupBuilder builder =
        JBPopupFactory.getInstance().createComponentPopupBuilder(pane, null);
    builder.setResizable(true);
    builder.createPopup().showCenteredInCurrentWindow(e.getProject());
  }

  public class MyRenderer implements ListCellRenderer<EditorInfo> {

    @Override
    public Component getListCellRendererComponent(JList<? extends EditorInfo> jList,
                                                  EditorInfo editorInfo, int i, boolean b,
                                                  boolean b1) {

      EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
      TextAttributes textColors = scheme.getAttributes(TextAttributesKey.find("DIFF_MODIFIED"));
      TitlePanel titlePanel = new TitlePanel();
      titlePanel.setText("TEST TITLE");
      editorInfo.editor.setHeaderComponent(titlePanel);
      editorInfo.editor.getMarkupModel().addLineHighlighter(12, 2, textColors);
      editorInfo.editor.getComponent().setPreferredSize(new Dimension(400, 200));
      editorInfo.editor.getScrollingModel().scrollTo(
          new LogicalPosition(editorInfo.line, 0), ScrollType.CENTER);
      return editorInfo.editor.getComponent();
    }
  }

  public class EditorInfo {

    public Editor editor;
    public int line;

    public EditorInfo(Editor editor, int line) {
      this.editor = editor;
      this.line = line;
    }
  }

}
