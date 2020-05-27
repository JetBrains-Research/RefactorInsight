package services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.refactoringminer.api.RefactoringType.ADD_METHOD_ANNOTATION;
import static org.refactoringminer.api.RefactoringType.CHANGE_ATTRIBUTE_TYPE;
import static org.refactoringminer.api.RefactoringType.CHANGE_PARAMETER_TYPE;
import static org.refactoringminer.api.RefactoringType.CHANGE_RETURN_TYPE;
import static org.refactoringminer.api.RefactoringType.CHANGE_VARIABLE_TYPE;
import static org.refactoringminer.api.RefactoringType.EXTRACT_AND_MOVE_OPERATION;
import static org.refactoringminer.api.RefactoringType.EXTRACT_INTERFACE;
import static org.refactoringminer.api.RefactoringType.EXTRACT_OPERATION;
import static org.refactoringminer.api.RefactoringType.EXTRACT_VARIABLE;
import static org.refactoringminer.api.RefactoringType.INLINE_OPERATION;
import static org.refactoringminer.api.RefactoringType.INLINE_VARIABLE;
import static org.refactoringminer.api.RefactoringType.MOVE_AND_RENAME_OPERATION;
import static org.refactoringminer.api.RefactoringType.MOVE_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.MOVE_CLASS;
import static org.refactoringminer.api.RefactoringType.MOVE_OPERATION;
import static org.refactoringminer.api.RefactoringType.MOVE_RENAME_CLASS;
import static org.refactoringminer.api.RefactoringType.MOVE_SOURCE_FOLDER;
import static org.refactoringminer.api.RefactoringType.PULL_UP_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.PULL_UP_OPERATION;
import static org.refactoringminer.api.RefactoringType.PUSH_DOWN_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.PUSH_DOWN_OPERATION;
import static org.refactoringminer.api.RefactoringType.REMOVE_METHOD_ANNOTATION;
import static org.refactoringminer.api.RefactoringType.RENAME_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.RENAME_CLASS;
import static org.refactoringminer.api.RefactoringType.RENAME_METHOD;
import static org.refactoringminer.api.RefactoringType.RENAME_PACKAGE;
import static org.refactoringminer.api.RefactoringType.RENAME_PARAMETER;
import static org.refactoringminer.api.RefactoringType.RENAME_VARIABLE;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.Executor;
import com.intellij.ui.treeStructure.Tree;
import data.RefactoringEntry;
import data.RefactoringInfo;
import git4idea.test.GitExecutor;
import git4idea.test.GitSingleRepoTest;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.rules.ErrorCollector;
import org.refactoringminer.api.RefactoringType;

/**
 * Extend GitSingleRepoTest
 * variables as myProject, repo, projectPath and much more are available from super classes
 * test method names have to begin with test!
 */
public class MiningServiceManualTest extends GitSingleRepoTest {

  private final String dirPath = "src/test/testData/miningService/";
  // each dir is a new commit
  private final String[] commitDirs = {
      "commit_2",
      "commit_3",
      "commit_4",
      "commit_5",
      "commit_6",
      "commit_7",
      "commit_8",
      "commit_9",
      "commit_10",
      "commit_11",
      "commit_12",
      "commit_13",
      "commit_14",
      "commit_15"
  };
  // and any number of RefactoringInfo predicates to check in specific commit
  // must have the same length as commitDirs
  private final Matcher<RefactoringEntry>[] matches = new Matcher[] {
      matcher(),
      matcher(
          ofType(PULL_UP_OPERATION),
          ofType(PUSH_DOWN_OPERATION),
          ofType(ADD_METHOD_ANNOTATION)
      ),
      matcher(
          ofType(ADD_METHOD_ANNOTATION),
          ofType(REMOVE_METHOD_ANNOTATION),
          ofType(EXTRACT_OPERATION)
      ),
      matcher(
          ofType(REMOVE_METHOD_ANNOTATION)
      ),
      matcher(
          ofType(RENAME_METHOD),
          ofType(EXTRACT_AND_MOVE_OPERATION)
      ),
      matcher(
          ofType(RENAME_CLASS),
          ofType(CHANGE_ATTRIBUTE_TYPE),
          ofType(CHANGE_PARAMETER_TYPE),
          ofType(CHANGE_RETURN_TYPE)
      ),
      matcher(
          ofType(MOVE_CLASS)
      ),
      matcher(
          ofType(MOVE_RENAME_CLASS),
          ofType(MOVE_SOURCE_FOLDER)
      ),
      matcher(
          ofType(RENAME_ATTRIBUTE),
          ofType(RENAME_PACKAGE),
          ofType(RENAME_PARAMETER)
      ),
      matcher(
          ofType(PULL_UP_ATTRIBUTE)
      ),
      matcher(
          ofType(PUSH_DOWN_ATTRIBUTE),
          ofType(INLINE_VARIABLE)
      ),
      matcher(
          ofType(EXTRACT_VARIABLE)
      ),
      matcher(
          ofType(RENAME_VARIABLE),
          ofType(CHANGE_PARAMETER_TYPE),
          ofType(CHANGE_VARIABLE_TYPE),
          ofType(MOVE_ATTRIBUTE),
          ofType(MOVE_OPERATION),
          ofType(MOVE_AND_RENAME_OPERATION)
      ),
      matcher(
          ofType(INLINE_OPERATION),
          ofType(EXTRACT_INTERFACE)
      )
  };
  private MyErrorCollector collector;
  private String[] hashes;
  private MiningService miner;

  public void setUp() throws Exception {
    super.setUp();
    assert matches.length == commitDirs.length;
    hashes = Arrays.stream(commitDirs)
        .map(dirPath::concat)
        .map(Paths::get)
        .map(dir -> {
          buildVFS(dir);
          return GitExecutor.addCommit(repo, "test commit");
        })
        .toArray(String[]::new);
    repo.update();
    miner = MiningService.getInstance(myProject);
    miner.mineAndWait(repo);
    collector = new MyErrorCollector();
  }

  public void testAll() throws Throwable {
    for (int i = 1; i < hashes.length; i++) {
      RefactoringEntry entry = miner.getEntry(hashes[i]);
      collector.checkThat(entry.getCommitId(), equalTo(hashes[i]));
      collector.checkThat(entry.getParents().get(0), equalTo(hashes[i - 1]));
      collector.checkThat("Issue in: " + commitDirs[i], entry, matches[i]);
    }
    collector.verify();
  }

  private Predicate<RefactoringInfo> ofType(RefactoringType type) {
    return r -> r.getType().equals(type);
  }

  private Matcher<RefactoringEntry> matcher(Predicate<RefactoringInfo>... predicates) {
    return new CustomMatcher<>("valid refactoring entries") {
      @Override
      public boolean matches(Object o) {
        if (o instanceof RefactoringEntry) {
          if (predicates.length == 0) {
            return true;
          }
          List<RefactoringInfo> refactorings = ((RefactoringEntry) o).getRefactorings();
          Tree tree = ((RefactoringEntry) o).buildTree();
          Object root = tree.getModel().getRoot();
          assertNotNull(tree.getCellRenderer()
              .getTreeCellRendererComponent(tree, root, false,
                  false, false, 0, false));
          int children = tree.getModel().getChildCount(root);
          int i = 0;
          while (i < children) {
            Object oo = tree.getModel().getChild(root, i);
            assertNotNull(tree.getCellRenderer()
                .getTreeCellRendererComponent(tree, oo, false,
                    false, false, 0, false));
            assertNotNull(tree.getCellRenderer()
                .getTreeCellRendererComponent(tree, tree.getModel().getChild(oo, 0), false,
                    false, true, 0, false));
            i++;
          }

          boolean res = true;
          for (Predicate<RefactoringInfo> p : predicates) {
            res &= refactorings.stream().anyMatch(p);
          }
          return res;
        }
        return false;
      }
    };
  }

  private void buildVFS(Path rootDir) {
    try {
      FileUtil.delete(Executor.child("src"));
      Files.walkFileTree(rootDir, new FileVisitor<>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Executor.touch(rootDir.relativize(file).toString(), Files.readString(file));
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class MyErrorCollector extends ErrorCollector {
    @Override
    public void verify() throws Throwable {
      super.verify();
    }
  }

}