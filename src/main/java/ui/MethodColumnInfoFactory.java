package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.util.ui.ColumnInfo;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.intellij.vcs.log.impl.HashImpl;
import com.intellij.vcs.log.impl.VcsProjectLog;
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
        return new ColumnInfo[]{
                new TimeOfCommit(),
                new NameInfo(),
                new TypeInfo()
        };
    }

    public Class[] getColumnClasses() {
        return new Class[]{TimeOfCommit.class, NameInfo.class, TypeInfo.class};
    }

    static class TimeOfCommit extends ColumnInfo<RefactoringInfo, String> {

        public TimeOfCommit() {
            super("Time");
        }

        @Nullable
        @Override
        public String valueOf(RefactoringInfo methodItem) {
            long timestamp = 0;
            try {
                timestamp = methodItem.getTimestamp(project);
            } catch (VcsException e) {
                e.printStackTrace();
            }
            return convertTime(timestamp);
        }

        @Nullable
        @Override
        public Comparator<RefactoringInfo> getComparator() {
            return new Comparator<RefactoringInfo>() {
                @Override
                public int compare(RefactoringInfo r1, RefactoringInfo r2) {
                    try {
                        return Long.compare(r2.getTimestamp(project), r1.getTimestamp(project));
                    } catch (VcsException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            };
        }

        public static String convertTime(long time){
            Date date = new Date(time);
            Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return format.format(date);
        }
    }

    static class NameInfo extends ColumnInfo<RefactoringInfo, String> {

        public NameInfo() {
            super("Name at That Time");
        }

        @Nullable
        @Override
        public String valueOf(RefactoringInfo methodItem) {
            return methodItem.getNameAfter();
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
            return methodItem.getType().toString();
        }

        @Nullable
        @Override
        public Comparator<RefactoringInfo> getComparator() {
            return Comparator.comparing(o -> o.getType().toString());
        }
    }
}
