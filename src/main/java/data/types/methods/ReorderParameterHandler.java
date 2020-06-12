package data.types.methods;

import com.intellij.openapi.util.Pair;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.ReorderParameterRefactoring;
import java.util.List;
import java.util.stream.IntStream;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class ReorderParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ReorderParameterRefactoring ref = (ReorderParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();
    List<VariableDeclaration> as = ref.getParametersBefore();
    List<VariableDeclaration> bs = ref.getParametersAfter();
    IntStream.range(0, Math.min(as.size(), bs.size()))
        .mapToObj(i -> new Pair<>(as.get(i), bs.get(i)))
        .forEach(x -> {
          if (!x.first.getVariableDeclaration().getType()
              .equals(x.second.getVariableDeclaration().getType())
              || !x.first.getVariableName().equals(x.second.getVariableName())) {
            info.addMarking(x.first.codeRange(), x.second.codeRange(), true);
          }
        });
    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
