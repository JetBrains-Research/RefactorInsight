package org.jetbrains.research.refactorinsight.data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;

/**
 * This is a DiffRequestGenerator.
 * Its implementations represent all types of diff requests that this plugin supports.
 * The DiffRequestGenerator is used to collect data from the RefactoringLines, to correct them,
 * and to create a diff request in order to display the data.
 */
public abstract class DiffRequestGenerator {

  transient List<RefactoringLine> lineMarkings = new ArrayList<>();

  public DiffRequestGenerator() {
  }

  public abstract SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info);

  public abstract void prepareJetBrainsRanges(List<RefactoringLine> lineMarkings);


  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
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

  /**
   * Corrects each line if necessary.
   *
   * @param before               text left diff window
   * @param mid                  text mid diff window
   * @param after                text right diff window
   * @param skipAnnotationsLeft  always true, except when remove or modify annotation happen
   * @param skipAnnotationsMid   true
   * @param skipAnnotationsRight always true, except when add or modify annotation happen
   */
  public void correct(String before, String mid, String after, boolean skipAnnotationsLeft,
                      boolean skipAnnotationsMid, boolean skipAnnotationsRight) {
    lineMarkings.forEach(l -> l
        .correctLines(before, mid, after, skipAnnotationsLeft, skipAnnotationsMid,
            skipAnnotationsRight));
    prepareJetBrainsRanges(lineMarkings);
  }
}
