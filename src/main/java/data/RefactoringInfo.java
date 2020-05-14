package data;

import com.google.gson.Gson;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  private String text;
  private String name;
  private String commitId;
  private String signatureBefore;
  private String signatureAfter;
  private RefactoringType type;
  private List<TrueCodeRange> leftSide;
  private List<TrueCodeRange> rightSide;
  private Map<String, String> renames;
  private int[] nameIndeces = {0, 0};

  /**
   * Constructor for refactoring info data structure.
   *
   * @param name of the refactoring
   * @param text of the refactoring
   * @param type of the refactoring
   * @param leftSide before refactoring data
   * @param rightSide after refactoring data
   */
  public RefactoringInfo(String name, String text, RefactoringType type, List<TrueCodeRange> leftSide,
                         List<TrueCodeRange> rightSide, String commitId, String signatureBefore,
                         String signatureAfter) {
    this.name = name;
    this.type = type;
    this.text = text;
    this.leftSide = leftSide;
    this.rightSide = rightSide;
    this.commitId = commitId;
    renames = new HashMap<>();
    this.signatureBefore = signatureBefore;
    this.signatureAfter = signatureAfter;
  }


  public RefactoringInfo() {
  }


  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    if (signatureAfter.equals("")) {
      renames.forEach((before, after) -> map.keySet().stream()
          .filter(x -> x.substring(0, x.lastIndexOf("."))
              .equals(before))
          .forEach(signature -> {
            String newKey = after + signature.substring(signature.lastIndexOf("."));
            map.put(newKey, map.getOrDefault(signature, new ArrayList<>()));
          }));
      return;
    }
    List<RefactoringInfo> refs = map.getOrDefault(signatureBefore, new LinkedList<>());
    map.remove(signatureBefore);
    refs.add(0, this);
    map.put(signatureAfter, refs);
  }

  public Map<String, String> getRenames() {
    return renames;
  }

  public String getName() {
    return name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getCommitId() {
    return commitId;
  }

  public RefactoringType getType() {
    return type;
  }

  public List<TrueCodeRange> getLeftSide() {
    return leftSide;
  }

  public List<TrueCodeRange> getRightSide() {
    return rightSide;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public String getSignatureBefore() {
    return signatureBefore;
  }

  public String getSignatureAfter() {
    return signatureAfter;
  }

  private void processType(RefactoringType type, Refactoring refactoring) {
    switch (type) {
      case RENAME_CLASS:
        RenameClassRefactoring renameClassRefactoring =
            (RenameClassRefactoring) refactoring;
        renames.put(renameClassRefactoring.getOriginalClassName(),
            renameClassRefactoring.getRenamedClassName());
        break;
      case MOVE_CLASS:
        MoveClassRefactoring moveClassRefactoring =
            (MoveClassRefactoring) refactoring;
        renames.put(moveClassRefactoring.getOriginalClassName(),
            moveClassRefactoring.getMovedClassName());
        break;
      case MOVE_RENAME_CLASS:
        MoveAndRenameClassRefactoring moveAndRenameClassRefactoring =
            (MoveAndRenameClassRefactoring) refactoring;
        renames.put(moveAndRenameClassRefactoring.getOriginalClassName(),
            moveAndRenameClassRefactoring.getRenamedClassName());
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
      default:
        break;
    }
  }
}
