package data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import data.RefactoringInfo;
import data.RefactoringLine;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class DiffRequestGenerator {

  transient List<RefactoringLine> lineMarkings = new ArrayList<>();

  public DiffRequestGenerator() {
  }

  public abstract SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info);


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

  public void correct(String before, String mid, String after) {
    lineMarkings.forEach(l -> l.correctLines(before, mid, after));
  }
}
