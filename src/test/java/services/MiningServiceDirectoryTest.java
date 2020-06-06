package services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsUser;
import com.intellij.vcs.log.impl.VcsCommitMetadataImpl;
import data.RefactoringEntry;
import git4idea.test.GitSingleRepoTest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.tree.TreeCellRenderer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import ui.windows.GitWindow;
import ui.renderer.CellRenderer;

/**
 * Extend GitSingleRepoTest
 * variables as myProject, repo, projectPath and much more are available from super classes
 * test method names have to begin with test!
 */
public class MiningServiceDirectoryTest extends GitSingleRepoTest {

  private MiningService miner;
  private String head;
  private RefactoringEntry entry;

  //doesnt have to be overwritten,
  // but if we want to setup stuff ourselves call super.setup() first
  //No @Before annotation
  public void setUp() throws Exception {
    super.setUp();
    miner = MiningService.getInstance(myProject);
    String thisDir = System.getProperty("user.dir");
    //Make files for source and destination directory
    File srcDir = new File(thisDir + "/src/test/testData/example-refactorings");
    File destDir = new File(projectPath);
    //Copy directory + contents
    try {
      FileUtils.copyDirectory(srcDir, destDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //rename gitdir to .git
    File oldGitDir = new File(projectPath + "/gitdir");
    File newGitDir = new File(projectPath + "/.git");
    try {
      FileUtils.deleteDirectory(newGitDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
    oldGitDir.renameTo(newGitDir);
    repo.update();
    miner.mineAndWait(repo);
    head = repo.getCurrentRevision();
    entry = miner.getEntry(head);
  }

  //Example of using an existing repo
  //note that the project directory has to contain the .git folder but renamed
  public void testDirectory() {
    assertNull(RefactoringEntry.fromString(""));
    assertNull(RefactoringEntry.fromString(null));

    assertEquals(1, entry.getRefactorings().size());
    assertTrue(miner.getRefactorings(head).length() > 0);

    assertThrows(IllegalArgumentException.class, () -> MiningService.getInstance(null));
    assertEquals(miner, MiningService.getInstance(myProject));
    assertNotNull(miner.getState());
    assertFalse(miner.getState().map.isEmpty());
    assertTrue(!miner.isMining());
    assertFalse(miner.getMethodHistory().isEmpty());
    assertThrows(NullPointerException.class, () -> miner.mineAndWait(null));
  }

  public void testTreeIsBuilt() {
    RefactoringEntry entry = miner.getEntry(head);
    Tree tree = entry.buildTree();
    tree.setCellRenderer(new CellRenderer());
    TreeCellRenderer cellRenderer = tree.getCellRenderer();
    Object root = tree.getModel().getRoot();
    assertNotNull(cellRenderer
        .getTreeCellRendererComponent(tree, root, false,
            false, false, 0, false));

    assertNotNull(entry.buildTree());
  }

  public void testMineAtCommit() {
    String head = repo.getCurrentRevision();
    Hash hash = createHashObject(head);

    Hash parent = mock(Hash.class);
    //return a random string
    when(parent.asString()).thenReturn("");
    List<Hash> parents = Arrays.asList(parent);
    VcsUser user = createTestVcsUser();

    //cannot be mocked since it cannot be null when calling methods
    VcsCommitMetadataImpl vcsCommitMetadata = new VcsCommitMetadataImpl(hash,
        parents, 0, repo.getRoot(), "subject",
        user, "message", user, 0);

    GitWindow gitWindow = mock(GitWindow.class);
    Mockito.doThrow(new NullPointerException()).when(gitWindow).refresh(any());
    miner.mineAtCommit(vcsCommitMetadata, myProject, gitWindow);
    verify(parent, new Times(1)).asString();
  }

  /**
   * Helper method that creates a VcsUser.
   * Used in testMineAtCommit to initialize a VcsCommitMetadata.
   *
   * @return helper VcsUser for testing purposes.
   */
  @NotNull
  private VcsUser createTestVcsUser() {
    return new VcsUser() {
      @NotNull
      @Override
      public String getName() {
        return "test";
      }

      @NotNull
      @Override
      public String getEmail() {
        return "test";
      }
    };
  }

  /**
   * Helper method that creates a Hash.
   * Used in testMineAtCommit to initialize a VcsCommitMetadata.
   *
   * @param head: to create a Hash for.
   * @return helper Hash for testing purposes.
   */
  @NotNull
  private Hash createHashObject(String head) {
    return new Hash() {
      @NotNull
      @Override
      public String asString() {
        return head;
      }

      @NotNull
      @Override
      public String toShortString() {
        return head;
      }
    };
  }
}