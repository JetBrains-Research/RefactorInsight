package utils;

import com.intellij.openapi.util.Disposer;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import data.RefactoringInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {

  /**
   * Used for storing and disposing the MainVcsLogs used for method history action.
   */
  private static ArrayList<MainVcsLogUi> logs = new ArrayList<>();

  /**
   * Method used for disposing the logs that were created and shown for the method history action.
   * Called when the project is closing.
   * Avoids memory leaks.
   */
  public static void dispose() {
    for (MainVcsLogUi log : logs) {
      Disposer.dispose(log);
    }
  }

  /**
   * Adds a MainVcsLogUI to the list.
   *
   * @param log to add.
   */
  public static void add(MainVcsLogUi log) {
    logs.add(log);
  }

  /**
   * Sorts the info list for a better displaying.
   *
   * @param infos to be sorted.
   */
  public static void chronologicalOrder(List<RefactoringInfo> infos) {
    infos.sort(new Comparator<RefactoringInfo>() {
      @Override
      public int compare(RefactoringInfo o1, RefactoringInfo o2) {
        return Long.compare(o1.getEntry().getTimeStamp(), o2.getEntry().getTimeStamp());
      }
    });
  }
}
