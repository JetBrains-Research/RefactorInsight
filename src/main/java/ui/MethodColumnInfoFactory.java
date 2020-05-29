package ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.text.JBDateFormat;
import com.intellij.util.ui.ColumnInfo;
import data.RefactoringInfo;
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

  }

  static class NameInfo extends ColumnInfo<RefactoringInfo, String> {

    public NameInfo() {
      super("Name");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      return methodItem.getDisplayableName();
    }
  }

  static class ClassInfo extends ColumnInfo<RefactoringInfo, String> {

    public ClassInfo() {
      super("Change");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      if (methodItem.getDisplayableElement() == null) {
        return methodItem.getDisplayableName();
      }
      return methodItem.getDisplayableElement();
    }
  }


  static class TypeInfo extends ColumnInfo<RefactoringInfo, String> {

    public TypeInfo() {
      super("Refactoring Scope");
    }

    @Nullable
    @Override
    public String valueOf(RefactoringInfo methodItem) {
      return methodItem.getType().getDisplayName();
    }
  }
}
