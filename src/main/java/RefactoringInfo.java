
import com.google.gson.Gson;
import gr.uom.java.xmi.diff.CodeRange;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;

public class RefactoringInfo {

    private String text;
    private RefactoringType type;
    private List<CodeRange> leftSide;
    private List<CodeRange> rightSide;

    public RefactoringInfo(Refactoring refactoring) {
        type = refactoring.getRefactoringType();
        text = refactoring.toString();
        leftSide = refactoring.leftSide();
        rightSide = refactoring.rightSide();
    }

    public String getText() {
        return text;
    }

    public RefactoringType getType() {
        return type;
    }

    public List<CodeRange> getLeftSide() {
        return leftSide;
    }

    public List<CodeRange> getRightSide() {
        return rightSide;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    public static RefactoringInfo fromString(String value){
        return new Gson().fromJson(value, RefactoringInfo.class);
    }

    public static String convert(Refactoring refactoring) {
        return new RefactoringInfo(refactoring).toString();
    }

    public void processType(RefactoringType type) {
        switch (type) {
            case RENAME_CLASS:
                break;
            case MOVE_CLASS:
                break;
            case MOVE_SOURCE_FOLDER:
                break;
            case RENAME_METHOD:
                break;
            case EXTRACT_OPERATION:
                break;
            case INLINE_OPERATION:
                break;
            case MOVE_OPERATION:
                break;
            case PULL_UP_OPERATION:
                break;
            case PUSH_DOWN_OPERATION:
                break;
            case MOVE_ATTRIBUTE:
                break;
            case MOVE_RENAME_ATTRIBUTE:
                break;
            case REPLACE_ATTRIBUTE:
                break;
            case PULL_UP_ATTRIBUTE:
                break;
            case PUSH_DOWN_ATTRIBUTE:
                break;
            case EXTRACT_INTERFACE:
                break;
            case EXTRACT_SUPERCLASS:
                break;
            case EXTRACT_SUBCLASS:
                break;
            case EXTRACT_CLASS:
                break;
            case EXTRACT_AND_MOVE_OPERATION:
                break;
            case MOVE_RENAME_CLASS:
                break;
            case RENAME_PACKAGE:
                break;
            case EXTRACT_VARIABLE:
                break;
            case INLINE_VARIABLE:
                break;
            case RENAME_VARIABLE:
                break;
            case RENAME_PARAMETER:
                break;
            case RENAME_ATTRIBUTE:
                break;
            case REPLACE_VARIABLE_WITH_ATTRIBUTE:
                break;
            case PARAMETERIZE_VARIABLE:
                break;
            case MERGE_VARIABLE:
                break;
            case MERGE_PARAMETER:
                break;
            case MERGE_ATTRIBUTE:
                break;
            case SPLIT_VARIABLE:
                break;
            case SPLIT_PARAMETER:
                break;
            case SPLIT_ATTRIBUTE:
                break;
            case CHANGE_RETURN_TYPE:
                break;
            case CHANGE_VARIABLE_TYPE:
                break;
            case CHANGE_PARAMETER_TYPE:
                break;
            case CHANGE_ATTRIBUTE_TYPE:
                break;
            case EXTRACT_ATTRIBUTE:
                break;
            case MOVE_AND_RENAME_OPERATION:
                break;
            case MOVE_AND_INLINE_OPERATION:
                break;
            case REMOVE_METHOD_ANNOTATION:
                break;
            case MODIFY_METHOD_ANNOTATION:
                break;
        }
    }
}
