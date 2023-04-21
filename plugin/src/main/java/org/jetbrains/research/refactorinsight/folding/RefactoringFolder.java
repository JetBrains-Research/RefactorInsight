package org.jetbrains.research.refactorinsight.folding;

import com.intellij.application.options.colors.highlighting.RendererWrapper;
import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.diff.DiffVcsDataKeys;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.base.DiffViewerBase;
import com.intellij.diff.tools.util.side.OnesideTextDiffViewer;
import com.intellij.diff.tools.util.side.ThreesideTextDiffViewer;
import com.intellij.diff.tools.util.side.TwosideTextDiffViewer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.github.pullrequest.comment.GHPRDiffReviewSupport;
import org.jetbrains.research.refactorinsight.actions.HideNonFunctionalChangesAction;
import org.jetbrains.research.refactorinsight.folding.handlers.*;
import org.jetbrains.research.refactorinsight.processors.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Folds discovered refactorings in code diffs.
 */
public class RefactoringFolder {
  static Map<String, FoldingHandler> foldingHandlers;
  static Set<FoldRegion> foldRegions;

  static {
    foldingHandlers = new HashMap<>();
    FoldingHandler moveOperationHandler = new MoveOperationFoldingHandler();
    foldingHandlers.put(RefactoringType.MOVE_OPERATION.getName(), moveOperationHandler);
    foldingHandlers.put(RefactoringType.PULL_UP_OPERATION.getName(), moveOperationHandler);
    foldingHandlers.put(RefactoringType.PUSH_DOWN_OPERATION.getName(), moveOperationHandler);
    foldingHandlers.put(RefactoringType.MOVE_AND_RENAME_OPERATION.getName(), moveOperationHandler);
    FoldingHandler inlineOperationHandler = new InlineOperationFoldingHandler();
    foldingHandlers.put(RefactoringType.INLINE_OPERATION.getName(), inlineOperationHandler);
    foldingHandlers.put(RefactoringType.MOVE_AND_INLINE_OPERATION.getName(), inlineOperationHandler);
    FoldingHandler extractOperationHandler = new ExtractOperationFoldingHandler();
    foldingHandlers.put(RefactoringType.EXTRACT_OPERATION.getName(), extractOperationHandler);
    foldingHandlers.put(RefactoringType.EXTRACT_AND_MOVE_OPERATION.getName(), extractOperationHandler);
    FoldingHandler moveClassHandler = new MoveClassFoldingHandler();
    foldingHandlers.put(RefactoringType.MOVE_CLASS.getName(), moveClassHandler);
    foldingHandlers.put(RefactoringType.MOVE_RENAME_CLASS.getName(), moveClassHandler);
    FoldingHandler extractClassHandler = new ExtractClassFoldingHandler();
    foldingHandlers.put(RefactoringType.EXTRACT_CLASS.getName(), extractClassHandler);
    foldingHandlers.put(RefactoringType.EXTRACT_SUBCLASS.getName(), extractClassHandler);
    foldRegions = new HashSet<>();
  }

  private RefactoringFolder() {}

  /**
   * Folds refactorings in the viewer if supported.
   *
   * @param viewer  Viewer of diff request.
   * @param request Associated diff request.
   */
  public static void foldRefactorings(@NotNull FrameDiffTool.DiffViewer viewer, @NotNull DiffRequest request) {
    if (!(request instanceof SimpleDiffRequest && viewer instanceof DiffViewerBase)) {
      return;
    }
    SimpleDiffRequest diffRequest = (SimpleDiffRequest) request;
    DiffViewerBase viewerBase = (DiffViewerBase) viewer;

    Project project = viewerBase.getProject();
    if (project == null) {
      return;
    }

    String commitId = getRevisionAfter(diffRequest);
    if (commitId == null) {
      return;
    }

    RefactoringEntry entry = MiningService.getInstance(project).get(commitId);
    if (entry == null) {
      return;
    }

    List<RefactoringInfo> foldableRefactorings =
        entry.getRefactorings().stream()
            .filter(info -> foldingHandlers.containsKey(info.getType()))
            .collect(Collectors.toList());

    if (viewerBase instanceof OnesideTextDiffViewer) {
      foldRefactorings(foldableRefactorings, (OnesideTextDiffViewer) viewerBase, project);
    } else if (viewerBase instanceof TwosideTextDiffViewer) {
      foldRefactorings(foldableRefactorings, (TwosideTextDiffViewer) viewerBase, project);
    } else if (viewerBase instanceof ThreesideTextDiffViewer) {
      foldRefactorings(foldableRefactorings, (ThreesideTextDiffViewer) viewerBase, project);
    }
  }

  public static boolean isRefactoringFoldRegion(FoldRegion region) {
    return foldRegions.contains(region);
  }

  /**
   * Folds only in the added files.
   */
  private static void foldRefactorings(@NotNull List<RefactoringInfo> foldableRefactorings,
                                       @NotNull OnesideTextDiffViewer viewer,
                                       @NotNull Project project) {
    modifyEditor(viewer.getEditor(), foldableRefactorings, project, false);
  }

  private static void foldRefactorings(@NotNull List<RefactoringInfo> foldableRefactorings,
                                       @NotNull TwosideTextDiffViewer viewer,
                                       @NotNull Project project) {
    modifyEditor(viewer.getEditor1(), foldableRefactorings, project, true);
    modifyEditor(viewer.getEditor2(), foldableRefactorings, project, false);
  }

  private static void foldRefactorings(@NotNull List<RefactoringInfo> foldableRefactorings,
                                       @NotNull ThreesideTextDiffViewer viewer,
                                       @NotNull Project project) {
    List<? extends EditorEx> editors = viewer.getEditors();

    modifyEditor(editors.get(0), foldableRefactorings, project, true);
    modifyEditor(editors.get(1), foldableRefactorings, project, false);
    modifyEditor(editors.get(2), foldableRefactorings, project, true);
  }

  private static void modifyEditor(@NotNull Editor editor,
                                   @NotNull List<RefactoringInfo> foldableRefactorings,
                                   @NotNull Project project,
                                   boolean before) {
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    if (psiFile == null) {
      return;
    }

    List<FoldingDescriptor> folds = foldableRefactorings.stream()
        .flatMap(info ->
            foldingHandlers.get(info.getType()).getFolds(info, psiFile, before).stream()
                .map(folding -> new Pair<>(info.getType(), folding))
        ).collect(
            Collectors.groupingBy(pair -> pair.second.getHintOffset(),
                Collectors.groupingBy(pair -> pair.first,
                    Collectors.mapping(pair -> pair.second,
                        Collectors.toList()))))
        .values().stream()
        .flatMap(map -> map.entrySet().stream())
        .map(group -> group.getValue().size() > 1
            ? foldingHandlers.get(group.getKey()).uniteFolds(group.getValue())
            : group.getValue().get(0))
        .collect(Collectors.toList());

    editor.getFoldingModel().runBatchFoldingOperation(() -> {
      for (FoldingDescriptor foldingDescriptor : folds) {
        FoldRegion value = editor.getFoldingModel()
            .addFoldRegion(foldingDescriptor.getFoldingStartOffset(), foldingDescriptor.getFoldingEndOffset(), "");
        if (value != null) {
          boolean hide = HideNonFunctionalChangesAction.isHide();
          value.setExpanded(!hide);
          value.setInnerHighlightersMuted(true);
        }
        foldRegions.add(value);

        RendererWrapper renderer = new RendererWrapper(new HintRenderer(foldingDescriptor.getHintText()), false);
        editor.getInlayModel().addBlockElement(
            foldingDescriptor.getHintOffset(),
            true, true, 1,
            renderer);
      }
    });
  }

  @Nullable
  private static String getRevisionAfter(@NotNull SimpleDiffRequest request) {
    if (request.getUserData(GHPRDiffReviewSupport.Companion.getKEY()) != null) {
      // Pull requests is not supported
      return null;
    }

    List<DiffContent> contents = request.getContents();
    if (contents.size() < 2) {
      return null;
    }
    Pair<FilePath, VcsRevisionNumber> userDataAfter = contents.get(1).getUserData(DiffVcsDataKeys.REVISION_INFO);
    if (userDataAfter == null) {
      return null;
    }
    return userDataAfter.second.asString();
  }
}
