package ui;

import com.intellij.util.ui.ColumnInfo;

import java.util.Comparator;

import data.RefactoringInfo;
import org.jetbrains.annotations.Nullable;

/**
 * All the column properties of the gui table are defined here.
 */
class MethodColumnInfoFactory {

    /**
     * Generates the column vector for the table model.
     *
     * @return ColumnInfo's
     */
    public ColumnInfo[] getColumnInfos() {
        return new ColumnInfo[]{
                new NameInfo(),
                new TypeInfo()
        };
    }

    public Class[] getColumnClasses() {
        return new Class[]{NameInfo.class, TypeInfo.class};
    }

    static class NameInfo extends ColumnInfo<RefactoringInfo, String> {

        public NameInfo() {
            super("Name");
        }

        @Nullable
        @Override
        public String valueOf(RefactoringInfo methodItem) {
            return methodItem.getName();
        }

        @Nullable
        @Override
        public Comparator<RefactoringInfo> getComparator() {
            return Comparator.comparing(RefactoringInfo::getName);
        }
    }

    static class TypeInfo extends ColumnInfo<RefactoringInfo, String> {

        public TypeInfo() {
            super("CC");
        }

        @Nullable
        @Override
        public String valueOf(RefactoringInfo methodItem) {
            return methodItem.getType().toString();
        }

        @Nullable
        @Override
        public Comparator<RefactoringInfo> getComparator() {
            return Comparator.comparing(o -> o.getType().toString());
        }
    }
}
