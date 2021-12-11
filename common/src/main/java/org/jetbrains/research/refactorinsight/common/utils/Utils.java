package org.jetbrains.research.refactorinsight.common.utils;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
//TODO import gr.uom.java.xmi.decomposition.AbstractStatement;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Utils {

    /**
     * Used for storing and disposing the MainVcsLogs used for method history action.
     */
    private static final ArrayList<Disposable> logs = new ArrayList<>();

    /**
     * Adds a Disposable object to the list.
     *
     * @param log to add.
     */
    public static void add(Disposable log) {
        logs.add(log);
    }

    /**
     * Finds the start and ending column of a word in a text.
     *
     * @param text Java Code
     * @param word Word to look for.
     * @param line In what line the word can be found.
     * @return Start and ending column in an int[]
     */
    public static int[] findColumns(String text, String word, int line) {
        String[] lines = text.split("\r\n|\r|\n");
        int startColumn = lines[line].indexOf(word) + 1;
        int endColumn = startColumn + word.length();
        return new int[]{startColumn, endColumn};
    }

    /**
     * Similar to find columns but starts from the back of the line.
     *
     * @param text Java code
     * @param word Word to look for
     * @param line Line to look in
     * @return Start and ending column in int[]
     */
    public static int[] findColumnsBackwards(String text, String word, int line) {
        String[] lines = text.split("\r\n|\r|\n");
        int startColumn = lines[line].lastIndexOf(word) + 1;
        int endColumn = startColumn + word.length();
        return new int[]{startColumn, endColumn};
    }

    /**
     * Skips javadoc for a method or class.
     *
     * @param text to search in.
     * @param line current line.
     * @return the actual line.
     */
    public static int skipJavadoc(String text, int line, boolean skipAnnotations) {
        String[] lines = text.split("\r\n|\r|\n");
        if (line >= lines.length) {
            System.out.println(text);

            System.out.println(line);
        }
        if (lines[line].contains("/**")) {
            for (int i = line + 1; i < lines.length; i++) {
                if (lines[i].contains("*/")) {
                    return skipAnnotations ? skipAnnotations(lines, i + 1) : i + 1;
                }
            }
        }
        return skipAnnotations ? skipAnnotations(lines, line) : line;
    }

    private static int skipAnnotations(String[] lines, int line) {
        for (int i = line; i < lines.length; i++) {
            if (lines[i].matches("((\\s|\\t)*@(\\w)*([(](.)*[)])*(\\s|\\t)*)+")
                    || lines[i].matches("(\\s|\\t)*")) {
                continue;
            } else {
                return i;
            }
        }
        return line;
    }

    /**
     * Calculates offset.
     *
     * @param text   to search in
     * @param line   line
     * @param column column
     * @return offset
     */
    public static int getOffset(String text, int line, int column) {
        int offset = 0;
        String[] lines = text.split("\r\n|\r|\n");
        if (lines.length <= line - 2) {
            line = lines.length;
        }
        for (int i = 0; i < line - 1; i++) {
            offset += lines[i].length() + 1;
        }
        return offset + column - 1;
    }

    /**
     * Returns last line count.
     *
     * @param text to search in
     * @return length of the text
     */
    public static int getMaxLine(String text) {
        return text.split("\r\n|\r|\n").length;
    }

    /**
     * Gets the main refactoring in the list for combining purposes.
     *
     * @param infos list of refactorings
     * @return RefactoringInfo
     */
    public static RefactoringInfo getMainRefactoringInfo(List<RefactoringInfo> infos) {
        RefactoringInfo info = null;
        if (infos.stream().anyMatch(ofType("Rename Attribute"))
                && infos.stream().anyMatch(ofType("Change Attribute Type"))) {
            info = infos.stream().filter(ofType("Rename Attribute")).findFirst().get();
            info.setType("Rename and Change Attribute Type");
        } else if (infos.stream().anyMatch(ofType("Rename Attribute"))) {
            info = infos.stream().filter(ofType("Rename Attribute")).findFirst().get();
        } else if (infos.stream().anyMatch(ofType("Change Attribute Type"))) {
            info = infos.stream().filter(ofType("Change Attribute Type")).findFirst().get();
        } else if (infos.stream().anyMatch(ofType("Change Variable Type"))) {
            info = infos.stream().filter(ofType("Change Variable Type")).findFirst().get();
            info.setType("Rename and Change Variable Type");
        } else if (infos.stream().anyMatch(ofType("Rename Parameter"))
                && infos.stream().anyMatch(ofType("Change Parameter Type"))) {
            info = infos.stream().filter(ofType("Rename Parameter")).findFirst().get();
            info.setType("Rename and Change Parameter Type");
        }
        return info;
    }

    private static Predicate<RefactoringInfo> ofType(String type) {
        return (r) -> r.getType().equals(type);
    }

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
     * Calculates the line of the package.
     *
     * @param text to search in.
     * @return line of the package.
     */
    public static int findPackageLine(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("package ")) {
                return i;
            } else if (lines[i].matches("^[a-zA-Z0-9]*$")) {
                return -1;
            }
        }
        return 0;
    }

}
