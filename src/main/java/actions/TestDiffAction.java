package actions;

import com.intellij.debugger.impl.JavaEditorTextProviderImpl;
import com.intellij.ide.actions.RecentLocationItem;
import com.intellij.ide.actions.RecentLocationsAction;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.ScrollingModelImpl;
import com.intellij.openapi.fileEditor.impl.IdeDocumentHistoryImpl;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.testFramework.fixtures.EditorHintFixture;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import gr.uom.java.xmi.LocationInfoProvider;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.Border;
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
    //TODO check if scrolling works if editor in a viewport
    Editor editor1 = EditorFactory.getInstance().createEditor(new DocumentImpl(text), e.getProject(), JavaFileType.INSTANCE, true);
    editor1.getComponent().setPreferredSize(new Dimension(400, 200));


    Editor editor2 = EditorFactory.getInstance().createEditor(new DocumentImpl(text), e.getProject(), JavaFileType.INSTANCE, true);
    editor2.getComponent().setPreferredSize(new Dimension(400, 200));
    ScrollingModelImpl scrollingModel = (ScrollingModelImpl) editor2.getScrollingModel();
    scrollingModel.getVerticalScrollBar().setOpaque(false);

    editor2.getScrollingModel().scrollTo(new LogicalPosition(20, 0), ScrollType.CENTER);
    scrollingModel.flushViewportChanges();
//    editor2.getComponent().repaint();

    JBList<Editor> list = new JBList<>(JBList.createDefaultListModel(editor1, editor2));
    list.setCellRenderer(new MyRenderer());
    JBScrollPane pane = new JBScrollPane(list);

    ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(pane, null);
    builder.setResizable(true);
    builder.createPopup().showCenteredInCurrentWindow(e.getProject());
  }

  public class MyRenderer implements ListCellRenderer<Editor> {


    @Override
    public Component getListCellRendererComponent(JList<? extends Editor> jList, Editor editor,
                                                  int i, boolean b, boolean b1) {

      return editor.getComponent();
    }
  }

}
