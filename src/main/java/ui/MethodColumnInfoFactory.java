package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.text.JBDateFormat;
import com.intellij.util.ui.ColumnInfo;
import data.RefactoringInfo;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import org.jetbrains.annotations.Nullable;

/**
 * All the column properties of the gui table are defined here.
 */
class MethodColumnInfoFactory {

  public static Project project;

  /**
   * Generates the column vector for the table model.
   *
   * @return ColumnInfo's
   */
  public ColumnInfo[] getColumnInfos() {
    return new ColumnInfo[] {
        new TimeOfCommit(),
        new NameInfo(),
        new ClassInfo(),
        new TypeInfo()
    };
  }

  public Class[] getColumnClasses() {
    return new Class[] {TimeOfCommit.class, NameInfo.class, ClassInfo.class, TypeInfo.class};
  }

  static class TimeOfCommit extends ColumnInfo<RefactoringInfo, String> {

    public TimeOfCommit() {
      super("Time");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      return JBDateFormat.getFormatter()
          .formatPrettyDateTime(methodItem.getTimestamp());
    }

    @Nullable
    @Override
    public Comparator<RefactoringInfo> getComparator() {
      return Comparator.comparing(RefactoringInfo::getTimestamp);
    }

  }

  static class NameInfo extends ColumnInfo<RefactoringInfo, String> {

    public NameInfo() {
      super("Name at That Time");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      String name = methodItem.getNameAfter();
      return name.substring(name.lastIndexOf('.') + 1);
    }

    @Nullable
    @Override
    public Comparator<RefactoringInfo> getComparator() {
      return Comparator.comparing(RefactoringInfo::getNameAfter);
    }
  }

  static class ClassInfo extends ColumnInfo<RefactoringInfo, String> {

    public ClassInfo() {
      super("Class at That Time");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      String name = methodItem.getNameAfter();
      name = name.substring(0, name.lastIndexOf('.'));
      return name.substring(name.lastIndexOf('.') + 1);
    }

    @Nullable
    @Override
    public Comparator<RefactoringInfo> getComparator() {
      return Comparator.comparing(RefactoringInfo::getNameAfter);
    }
  }


  static class TypeInfo extends ColumnInfo<RefactoringInfo, String> {

    public TypeInfo() {
      super("Refactoring Type");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      return methodItem.getType().toString().toLowerCase().replace('_', ' ');
    }

    @Nullable
    @Override
    public Comparator<RefactoringInfo> getComparator() {
      return Comparator.comparing(o -> o.getType().toString());
    }
  }
}
