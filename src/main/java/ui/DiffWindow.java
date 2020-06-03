package ui;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffContext;
import com.intellij.diff.DiffManager;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.simple.ThreesideDiffChangeBase;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import data.RefactoringInfo;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DiffWindow extends com.intellij.diff.DiffExtension {

  public static Key<RefactoringInfo> REFACTORING_INFO =
      Key.create("refactoringMiner.RefactoringInfo");
  public static Key<Integer[]> MAX_RANGE_VALUES =
      Key.create("refactoringMiner.maxRangeValues");

  /**
   * Requests diff window to show specific refactoring with two editors.
   *
   * @param left    Left text as String
   * @param right   Right text as String
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static void showDiff(String left, String right, RefactoringInfo info, Project project) {
    DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
    DiffContent diffContentBefore = myDiffContentFactory.create(project, left,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentAfter = myDiffContentFactory.create(project, right,
        JavaClassFileType.INSTANCE);

    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        diffContentBefore, diffContentAfter, info.getLeftPath(), info.getRightPath());

    int lineCountBefore = left.split("\r\n|\r|\n").length + 1;
    int lineCountAfter = right.split("\r\n|\r|\n").length + 1;
    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
        (text1, text2, policy, innerChanges, indicator) ->
            info.getTwoSidedLineMarkings(lineCountBefore, lineCountAfter));

    DiffManager.getInstance().showDiff(project, request);
  }

  /**
   * Requests diff window to show specific refactoring with three editors.
   *
   * @param left    Left text as String
   * @param mid     Mid test as String
   * @param right   Right text as String
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static void showDiff(String left, String mid, String right, RefactoringInfo info,
                              Project project) {
    DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
    DiffContent diffContentLeft = myDiffContentFactory.create(project, left,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentMid = myDiffContentFactory.create(project, mid,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentRight = myDiffContentFactory.create(project, right,
        JavaClassFileType.INSTANCE);

    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        diffContentLeft, diffContentMid, diffContentRight, info.getLeftPath(), info.getMidPath(),
        info.getRightPath());

    Integer[] maxRangeValues = new Integer[3];

    maxRangeValues[0] = left.split("\r\n|\r|\n").length + 1;
    maxRangeValues[1] = mid.split("\r\n|\r|\n").length + 1;
    maxRangeValues[2] = right.split("\r\n|\r|\n").length + 1;

    request.putUserData(REFACTORING_INFO, info);
    request.putUserData(MAX_RANGE_VALUES, maxRangeValues);

    DiffManager.getInstance().showDiff(project, request);

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
    RefactoringInfo info = request.getUserData(REFACTORING_INFO);
    if (info == null) {
      return;
    }
    Integer[] maxValues = request.getUserData(MAX_RANGE_VALUES);
    SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
    myViewer.addListener(new MyDiffViewerListener(myViewer, info, maxValues));
  }

  public class MyDiffViewerListener extends DiffViewerListener {

    private SimpleThreesideDiffViewer viewer;
    private RefactoringInfo info;
    private Integer[] maxValues;

    /**
     * EventListener for DiffWindow finishing diff calculation.
     *
     * @param viewer    DiffViewer
     * @param info      RefactoringInfo
     * @param maxValues TextLengths
     */
    public MyDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                RefactoringInfo info, Integer[] maxValues) {
      this.info = info;
      this.viewer = viewer;
      this.maxValues = maxValues;
    }

    @Override
    protected void onAfterRediff() {
      List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
      oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
      oldMarkings.clear();

      List<SimpleThreesideDiffChange> newMarkings =
          info.getThreeSidedLineMarkings(maxValues[0], maxValues[1], maxValues[2], viewer);

      oldMarkings.addAll(newMarkings);

    }
  }
}
