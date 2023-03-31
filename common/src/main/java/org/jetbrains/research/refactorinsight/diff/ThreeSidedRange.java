package org.jetbrains.research.refactorinsight.diff;

import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.util.text.MergeInnerDifferences;
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.ThreeSide;
import com.intellij.openapi.util.TextRange;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.jetbrains.research.refactorinsight.utils.StringUtils.*;

/**
 * Used to hold information about highlighting for a three sided diff window.
 */
public class ThreeSidedRange {

    List<TextRange> left;
    List<TextRange> mid;
    List<TextRange> right;

    VisualizationType type;

    MergeLineFragment fragment;

    /**
     * Constructor for ThreeSidedRange.
     *
     * @param left     text ranges.
     * @param mid      text ranges.
     * @param right    text ranges.
     * @param type     visualisation type.
     * @param fragment MergeLineFragment.
     */
    public ThreeSidedRange(List<TextRange> left, List<TextRange> mid, List<TextRange> right,
                           VisualizationType type,
                           MergeLineFragment fragment) {
        this.left = left;
        this.mid = mid;
        this.right = right;
        this.fragment = fragment;
        this.type = type;
    }

    public TextRange getLeft() {
        return left.get(0);
    }

    public TextRange getMid() {
        return mid.get(0);
    }

    public TextRange getRight() {
        return right.get(0);
    }

    private static List<TextRange> deStringify(String value) {
        String regex = delimiter(RANGE, true);
        String[] tokens = value.split(regex);
        assert tokens.length % 2 == 0;
        return IntStream.range(0, tokens.length / 2).map(i -> i * 2).mapToObj(i ->
                        new TextRange(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1])))
                .collect(Collectors.toList());
    }

    /**
     * Deserializes an {@link ThreeSidedRange} instance.
     *
     * @param value string.
     * @return range.
     */
    public static ThreeSidedRange fromString(String value) {
        String regex = delimiter(FRAG, true);
        String[] tokens = value.split(regex);
        return new ThreeSidedRange(
                deStringify(tokens[7]),
                deStringify(tokens[8]),
                deStringify(tokens[9]),
                VisualizationType.valueOf(tokens[0]),
                new MergeLineFragmentImpl(
                        Integer.parseInt(tokens[1]),
                        Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Integer.parseInt(tokens[5]),
                        Integer.parseInt(tokens[6])
                ));
    }

    public SimpleThreesideDiffChange getDiffChange(SimpleThreesideDiffViewer viewer) {
        return new SimpleThreesideDiffChange(fragment, getMergeConflictType(type),
                new MergeInnerDifferences(left, mid, right), viewer);
    }

    @Override
    public String toString() {
        return Stream.of(type.toString(),
                fragment.getStartLine(ThreeSide.LEFT),
                fragment.getEndLine(ThreeSide.LEFT),
                fragment.getStartLine(ThreeSide.BASE),
                fragment.getEndLine(ThreeSide.BASE),
                fragment.getStartLine(ThreeSide.RIGHT),
                fragment.getEndLine(ThreeSide.RIGHT),
                stringify(left),
                stringify(mid),
                stringify(right)
        ).map(String::valueOf).collect(Collectors.joining(delimiter(FRAG)));
    }

    private String stringify(List<TextRange> list) {
        return list.stream()
                .map(r -> r.getStartOffset() + delimiter(RANGE) + r.getEndOffset())
                .collect(Collectors.joining(delimiter(RANGE)));
    }

    private MergeConflictType getMergeConflictType(VisualizationType type) {
        switch (type) {
            case LEFT:
                return new MergeConflictType(MergeConflictType.Type.MODIFIED, true, false);
            case RIGHT:
                return new MergeConflictType(MergeConflictType.Type.INSERTED, false, true);
            default:
                return new MergeConflictType(MergeConflictType.Type.MODIFIED, true, true);
        }
    }
}
