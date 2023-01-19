package org.jetbrains.research.refactorinsight.data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;

/**
 * Collects data from the {@link RefactoringLine} instances, corrects them if needed,
 * and creates {@link DiffRequest} in order to visualize the detected refactorings.
 */
public abstract class DiffRequestGenerator {

  transient List<RefactoringLine> lineMarkings = new ArrayList<>();

  public DiffRequestGenerator() {
  }

  public abstract SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info);

  public abstract void prepareRanges(List<RefactoringLine> lineMarkings);

  /**
   * Add line marking for DiffWindow used to display refactorings.
   * Includes a possibility for sub-highlighting.
   */
  public void addMarking(CodeRange left, CodeRange mid, CodeRange right,
                         RefactoringLine.VisualisationType type,
                         Consumer<RefactoringLine> offsetFunction,
                         RefactoringLine.MarkingOption option,
                         boolean hasColumns) {

    RefactoringLine line = new RefactoringLine(left, mid, right, type, option, hasColumns);
    if (offsetFunction != null) {
      offsetFunction.accept(line);
    }
    lineMarkings.add(line);
  }

  public List<RefactoringLine> getMarkings() {
    return lineMarkings;
  }

  public abstract boolean containsElement(int lineNumber, int textOffset, boolean isRight);

  /**
   * Corrects each line if needed.
   *
   * @param before               text left diff window.
   * @param mid                  text mid diff window.
   * @param after                text right diff window.
   * @param skipAnnotationsLeft  always true, except when Remove or Modify Annotation refactorings happen.
   * @param skipAnnotationsMid   true.
   * @param skipAnnotationsRight always true, except when Add or Modify Annotation refactoring happen.
   */
  public void correct(String before, String mid, String after, boolean skipAnnotationsLeft,
                      boolean skipAnnotationsMid, boolean skipAnnotationsRight) {
    lineMarkings.forEach(l -> l
        .correctLines(before, mid, after, skipAnnotationsLeft, skipAnnotationsMid,
                      skipAnnotationsRight));
    prepareRanges(lineMarkings);
  }
}
