package org.jetbrains.research.refactorinsight.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextUtils {
    /**
     * Removes the extra part of the file path.
     *
     * @param path path to the file.
     * @return fixed path.
     */
    public static String fixPath(String path) {
        if (path != null && path.contains(".gradle/caches/")) {
            return path.substring(path.lastIndexOf("/bin/") + 5);
        } else {
            return path;
        }
    }

    /**
     * Checks and corrects the ranges returned by RefactoringMiner.
     *
     * @param info    refactoring info
     * @param project the open project
     * @return the corrected RefactoringInfo
     */
    public static RefactoringInfo check(RefactoringInfo info, Project project) {
        //check for refactorings without line markings
        // such as move source folder or rename package
        if (info.getLeftPath() == null || info.getRightPath() == null) {
            return info;
        }

        FilePath beforePath = new LocalFilePath(
                project.getBasePath() + "/"
                        + info.getLeftPath(), false);
        FilePath midPath = !info.isThreeSided() ? null : new LocalFilePath(
                project.getBasePath() + "/"
                        + info.getMidPath(), false);
        FilePath afterPath = new LocalFilePath(
                project.getBasePath() + "/"
                        + info.getRightPath(), false);
        GitRevisionNumber afterNumber = new GitRevisionNumber(info.getCommitId());
        GitRevisionNumber beforeNumber = new GitRevisionNumber(info.getParent());


        try {
            String after = GitContentRevision
                    .createRevision(afterPath, afterNumber, project).getContent();

            if (!info.isMoreSided()) {
                String before = GitContentRevision
                        .createRevision(beforePath, beforeNumber, project).getContent();
                String mid = !info.isThreeSided() ? null : GitContentRevision
                        .createRevision(midPath, afterNumber, project).getContent();

                info.correctLines(before, mid, after);
            } else {
                List<String> befores = new ArrayList<>();
                for (Pair<String, Boolean> pathPair : info.getMoreSidedLeftPaths()) {
                    GitRevisionNumber number = pathPair.second ? afterNumber : beforeNumber;
                    FilePath filePath =
                            new LocalFilePath(project.getBasePath() + "/" + fixPath(pathPair.first), false);
                    befores.add(
                            GitContentRevision.createRevision(filePath, number, project).getContent());
                }
                info.correctMoreSidedLines(befores, after);
            }
        } catch (VcsException e) {
            e.printStackTrace();
        }

        return info;
    }

    /**
     * Skip packages in qualified name.
     * Assumes the package name starts with a lowercase letter and the class name starts with an uppercase letter.
     */
    @NotNull
    public static String skipPackages(@NotNull String qualifiedName) {
        return Arrays.stream(qualifiedName.split("\\."))
                .dropWhile(element -> element.isEmpty() || Character.isLowerCase(element.charAt(0)))
                .collect(Collectors.joining("."));
    }

    /**
     * Get function simple name with empty parenthesis.
     */
    @NotNull
    public static String functionSimpleName(@NotNull String qualifiedName) {
        int nameBegin = qualifiedName.lastIndexOf('.') + 1;
        int nameEnd = qualifiedName.indexOf('(', nameBegin);
        return qualifiedName.substring(nameBegin, nameEnd) + "()";
    }

    /**
     * Sorts the info list for a better displaying.
     *
     * @param infos to be sorted.
     */
    public static void chronologicalOrder(List<RefactoringInfo> infos) {
        infos.sort(Comparator.comparingLong(o -> o.getEntry().getTimeStamp()));
    }

    /**
     * Calculates the version of the project by computing the hash code of the existing classes.
     *
     * @return the current version.
     */
    public static String version() {
        return RefactorInsightBundle.message("version") + String.valueOf(Stream.of(
                        //all classes that can change
                        RefactoringEntry.class,
                        RefactoringInfo.class,
                        RefactoringLine.class,
                        RefactoringLine.RefactoringOffset.class
                ).flatMap(c -> Arrays.stream(c.getDeclaredFields())
                        .map(Field::getGenericType)
                        .map(Type::getTypeName)
                ).collect(Collectors.toList()))
                .hashCode();
    }
}
