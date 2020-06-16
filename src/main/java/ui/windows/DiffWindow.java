package ui.windows;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffContext;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.simple.ThreesideDiffChangeBase;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TitlePanel;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.ui.components.JBList;
import data.RefactoringEntry;
import data.RefactoringInfo;
import data.diff.MoreSidedDiffRequestGenerator;
import data.diff.ThreeSidedRange;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.jetbrains.annotations.NotNull;

public class DiffWindow extends com.intellij.diff.DiffExtension {

  public static Key<List<ThreeSidedRange>> THREESIDED_RANGES =
      Key.create("refactoringMiner.List<ThreeSidedRange>");
  public static Key<List<MoreSidedDiffRequestGenerator.Data>> MORESIDED_RANGES =
      Key.create("refactoringMiner.List<MoreSidedDiffRequestGenerator.Data>");
  public static Key<Boolean> REFACTORING =
      Key.create("refactoringMiner.isRefactoringDiff");


  /**
   * Requests diff window to show specific refactoring with two editors.
   *
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static void showDiff(Collection<Change> changes, RefactoringInfo info,
                              Project project, RefactoringEntry entry) {
    final Predicate<RefactoringInfo> showable =
        i -> !i.isHidden() && i.getLeftPath() != null;
    List<DiffRequest> requests = entry.getRefactorings().stream()
        .filter(showable)
        .map(i -> i.generate(getDiffContents(changes, i, project)))
        .collect(Collectors.toList());
    DiffRequestChain chain = new SimpleDiffRequestChain(requests);
    final int index = entry.getRefactorings().stream()
        .filter(showable).collect(Collectors.toList()).indexOf(info);
    if (index != -1) {
      chain.setIndex(index);
      DiffManager.getInstance().showDiff(project, chain,
          new DiffDialogHints(WindowWrapper.Mode.FRAME));
    }
  }


  private static DiffContent[] getDiffContents(Collection<Change> changes,
                                               RefactoringInfo info, Project project) {
    if (info.getLeftPath() == null) {
      return null;
    }
    return info.isMoreSided() ? getMoreSidedDiffContents(changes, info, project) :
        getStandardDiffContents(changes, info, project);
  }

  /**
   * This method is for "More Sided" refactoring diff.
   */
  private static DiffContent[] getMoreSidedDiffContents(Collection<Change> changes,
                                                        RefactoringInfo info, Project project) {
    try {
      DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
      ArrayList<DiffContent> contentList = new ArrayList<>();
      for (Change change : changes) {
        if (change.getAfterRevision() != null
            && change.getAfterRevision().getFile().getPath().contains(info.getRightPath())) {
          contentList.add(myDiffContentFactory
              .create(project, change.getAfterRevision().getContent(),
                  JavaClassFileType.INSTANCE));
          break;
        }
      }
      for (String path : info.getMoreSidedLeftPaths()) {
        for (Change change : changes) {
          if (change.getBeforeRevision() != null
              && change.getBeforeRevision().getFile().getPath().contains(path)) {
            contentList.add(myDiffContentFactory
                .create(project, change.getBeforeRevision().getContent(),
                    JavaClassFileType.INSTANCE));
            break;
          }
        }
      }
      return contentList.toArray(new DiffContent[contentList.size()]);
    } catch (VcsException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * This is contents getter is for standard two or three sided refactoring diff.
   */
  private static DiffContent[] getStandardDiffContents(Collection<Change> changes,
                                                       RefactoringInfo info, Project project) {
    try {
      DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
      DiffContent[] contents = {null, null, null};
      for (Change change : changes) {
        if (change.getBeforeRevision() != null) {
          if (change.getBeforeRevision().getFile().getPath().contains(info.getLeftPath())) {
            contents[0] = myDiffContentFactory.create(project,
                change.getBeforeRevision().getContent(),
                JavaClassFileType.INSTANCE);
          }
        }
        if (info.isThreeSided()
            && change.getAfterRevision() != null
            && change.getAfterRevision().getFile().getPath().contains(info.getMidPath())) {
          contents[1] = myDiffContentFactory.create(project,
              change.getAfterRevision().getContent(),
              JavaClassFileType.INSTANCE);
        }
        if (change.getAfterRevision() != null
            && change.getAfterRevision().getFile().getPath().contains(info.getRightPath())) {
          contents[2] = myDiffContentFactory.create(project,
              change.getAfterRevision().getContent(),
              JavaClassFileType.INSTANCE);
        }
      }
      return contents;

    } catch (VcsException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * IntelliJ Diff Extension.
   * This is needed to obtain the viewer object.
   * Sets a listener which is activated once classical diff is calculated and
   * code ranges can be replaced with refactoring specific ranges.
   */
  @Override
  public void onViewerCreated(@NotNull FrameDiffTool.DiffViewer viewer,
                              @NotNull DiffContext context, @NotNull DiffRequest request) {
    //Check diff viewer type for refactoring
    Boolean isRefactoring = request.getUserData(REFACTORING);
    if (isRefactoring == null) {
      return;
    }

    //Check if diff viewer is three sided
    List<ThreeSidedRange> threeSidedRanges = request.getUserData(THREESIDED_RANGES);
    if (threeSidedRanges != null) {
      SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
      myViewer.getTextSettings().setExpandByDefault(false);
      myViewer.addListener(new MyDiffViewerListener(myViewer, threeSidedRanges));
      return;
    }

    //Check if diff viewer is more sided
    List<MoreSidedDiffRequestGenerator.Data> moreSidedRanges =
        request.getUserData(MORESIDED_RANGES);
    SimpleDiffViewer myViewer = (SimpleDiffViewer) viewer;
    if (moreSidedRanges != null) {
      //Highlight right part
      EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
      TextAttributes textColors = scheme.getAttributes(TextAttributesKey.find("DIFF_MODIFIED"));
      MoreSidedDiffRequestGenerator.Data rightData = moreSidedRanges.get(0);
      for (int i = rightData.startLine; i < rightData.endLine; i++) {
        myViewer.getEditor2().getMarkupModel().addLineHighlighter(i, 2, textColors);
      }
      List<Pair<MoreSidedDiffRequestGenerator.Data, Project>> pairs =
          moreSidedRanges.subList(1, moreSidedRanges.size()).stream()
          .map(data -> new Pair<>(data, myViewer.getProject()))
          .collect(Collectors.toList());
      //Generate Left UI
      JBList<Pair<MoreSidedDiffRequestGenerator.Data, Project>> editorList = new JBList<>(JBList.createDefaultListModel(pairs));
      JPanel leftPanel = (JPanel) myViewer.getEditor1().getComponent();
      leftPanel.remove(0);
      leftPanel.add(editorList);
      return;
    }

    //Asume diff viewer is two sided
    myViewer.getTextSettings().setExpandByDefault(false);

  }

  public class MoreSidedRenderer implements ListCellRenderer<Pair<MoreSidedDiffRequestGenerator.Data, Project>> {

    @Override
    public Component getListCellRendererComponent(
        JList<? extends Pair<MoreSidedDiffRequestGenerator.Data, Project>> jList,
        Pair<MoreSidedDiffRequestGenerator.Data, Project> pair, int i, boolean b,
        boolean b1) {
      DocumentContent content = (DocumentContent) pair.first.content;

      Editor editor = EditorFactory.getInstance().createEditor(content.getDocument(),
          pair.second, JavaFileType.INSTANCE, true);
      EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
      TextAttributes textColors = scheme.getAttributes(TextAttributesKey.find("DIFF_MODIFIED"));
      TitlePanel titlePanel = new TitlePanel("path here", null);
      editor.setHeaderComponent(titlePanel);
      for (int ind = pair.first.startLine; ind < pair.first.endLine; ind++) {
        editor.getMarkupModel().addLineHighlighter(ind, 2, textColors);
      }
      int editorSize = editor.getLineHeight() * (pair.first.endLine - pair.first.startLine + 2);
      editor.getComponent().setPreferredSize(new Dimension(400, editorSize));
      editor.getScrollingModel().scrollTo(
          new LogicalPosition(pair.first.startLine, 0), ScrollType.CENTER);
      return editor.getComponent();
    }
  }

  public static class MyDiffViewerListener extends DiffViewerListener {

    private final SimpleThreesideDiffViewer viewer;
    private final List<ThreeSidedRange> ranges;

    /**
     * EventListener for DiffWindow finishing diff calculation.
     *
     * @param viewer DiffViewer
     * @param ranges List of ThreeSidedRanges
     */
    public MyDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                List<ThreeSidedRange> ranges) {
      this.ranges = ranges;
      this.viewer = viewer;
    }

    @Override
    protected void onAfterRediff() {
      List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
      oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
      oldMarkings.clear();
      oldMarkings.addAll(ranges.stream()
          .map(r -> r.getDiffChange(viewer))
          .collect(Collectors.toList())
      );
    }
  }
}
