package ui;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import data.RefactoringEntry;
import data.RefactoringInfo;
import diff.FileDiffInfo;
import diff.HalfDiffInfo;
import diff.LineRange;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.NotNull;
import services.MiningService;

public class GitWindow extends ToggleAction {

  Project project;
  AnActionEvent event;
  DiffContentFactoryEx myDiffContentFactory;
  private ChangesTree changesTree;
  private JBViewport viewport;
  private boolean selected = false;
  private VcsLogGraphTable table;
  private JBScrollPane scrollPane;
  private MiningService miningService;

  private void setUp(@NotNull AnActionEvent e) {
    VcsLogChangesBrowser changesBrowser =
        (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
    changesTree = changesBrowser.getViewer();
    MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

    project = e.getProject();

    miningService = project.getService(MiningService.class);

    table = logUI.getTable();
    table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());

    event = e;

    myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();

    viewport = (JBViewport) changesTree.getParent();

    scrollPane = new JBScrollPane(new JBLabel("Select a commit to view refactorings"));

  }

  private void toRefactoringView(@NotNull AnActionEvent e) {
    System.out.println("Button ON");
    e.getProject().getService(MiningService.class).loaded();
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if(index != -1) {
      scrollPane.getViewport().setView(buildList(index));
    }
    viewport.setView(scrollPane);
  }

  private void toChangesView(@NotNull AnActionEvent e) {
    viewport.setView(changesTree);
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return selected;
  }

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    if (changesTree == null) {
      setUp(e);
    }
    if (state) {
      toRefactoringView(e);
    } else {
      toChangesView(e);
    }
    selected = state;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    super.update(e);
  }

  private JBList buildList(int index) {
    String commitId = table.getModel().getCommitId(index).getHash().asString();

    List<RefactoringInfo> refs =
        RefactoringEntry.fromString(miningService.getRefactorings(commitId))
            .getRefactorings();
    String[] names = refs.stream()
        .map(r -> r != null ? r.getName() : "not mined, DON'T CLICK!!")
        .toArray(String[]::new);

    JBList<String> list = new JBList<>(names);

    MouseAdapter mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          showDiff(index, refs.get(list.locationToIndex(e.getPoint())));
        }
      }
    };
    list.addMouseListener(mouseListener);
    return list;
  }

  private void showDiff(int index, RefactoringInfo ri) {
    Collection<Change> changes = table.getModel().getFullDetails(index).getChanges();

    List<HalfDiffInfo> leftDiffs = changes
        .stream()
        .map(Change::getBeforeRevision)
        .filter(Objects::nonNull)
        .map(c -> {
          List<LineRange> ranges = new ArrayList<>();
          ri.getLeftSide().forEach(cr -> {
            if ((project.getBasePath() + "/" + cr.getFilePath()).equals(c.getFile().getPath())) {
              ranges.add(new LineRange(cr.getStartLine() - 1, cr.getEndLine()));
            }
          });
          return new HalfDiffInfo(ranges, c);
        }).filter(hdi -> !hdi.getRanges().isEmpty())
        .collect(Collectors.toList());

    List<HalfDiffInfo> rightDiffs = changes
        .stream()
        .map(Change::getAfterRevision)
        .filter(Objects::nonNull)
        .map(c -> {
          List<LineRange> ranges = new ArrayList<>();
          ri.getRightSide().forEach(cr -> {
            if ((project.getBasePath() + "/" + cr.getFilePath()).equals(c.getFile().getPath())) {
              ranges.add(new LineRange(cr.getStartLine() - 1, cr.getEndLine()));
            }
          });
          return new HalfDiffInfo(ranges, c);
        }).filter(hdi -> !hdi.getRanges().isEmpty())
        .collect(Collectors.toList());

    List<FileDiffInfo> diffInfos = leftDiffs
        .stream()
        .flatMap(left -> rightDiffs.stream()
            .map(right -> new FileDiffInfo(left, right)))
        .collect(Collectors.toList());

    diffInfos.forEach(diffInfo -> {
      String contentBefore = diffInfo.getLeftContent();
      String contentAfter = diffInfo.getRightContent();

      DiffContent d1 = contentBefore != null
          ? myDiffContentFactory.create(project, contentBefore, JavaClassFileType.INSTANCE)
          : myDiffContentFactory.createEmpty();

      DiffContent d2 = contentAfter != null
          ? myDiffContentFactory.create(project, contentAfter, JavaClassFileType.INSTANCE)
          : myDiffContentFactory.createEmpty();

      SimpleDiffRequest request = new SimpleDiffRequest("Refactorings",
          d1, d2, diffInfo.getLeftPath(), diffInfo.getRightPath());

      request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
          (text1, text2, policy, innerChanges, indicator)
              -> diffInfo.getDiffFragments());

      DiffManager.getInstance().showDiff(project, request);
    });
  }

  class CommitSelectionListener implements ListSelectionListener {
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
      if (listSelectionEvent.getValueIsAdjusting()) {
        return;
      }
      DefaultListSelectionModel selectionModel =
          (DefaultListSelectionModel) listSelectionEvent.getSource();

      int beginIndex = selectionModel.getMinSelectionIndex();
      int endIndex = selectionModel.getMaxSelectionIndex();

      if (beginIndex != -1 || endIndex != -1) {
        scrollPane.getViewport().setView(buildList(beginIndex));
      }
    }
  }


}