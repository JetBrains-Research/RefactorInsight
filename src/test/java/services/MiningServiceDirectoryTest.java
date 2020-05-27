package services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsUser;
import com.intellij.vcs.log.impl.VcsCommitMetadataImpl;
import data.RefactoringEntry;
import git4idea.test.GitSingleRepoTest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import ui.GitWindow;

/**
 * Extend GitSingleRepoTest
 * variables as myProject, repo, projectPath and much more are available from super classes
 * test method names have to begin with test!
 */
public class MiningServiceDirectoryTest extends GitSingleRepoTest {

  private MiningService miner;

  //doesnt have to be overwritten,
  // but if we want to setup stuff ourselves call super.setup() first
  //No @Before annotation
  public void setUp() throws Exception {
    super.setUp();
    miner = MiningService.getInstance(myProject);
    String thisDir = System.getProperty("user.dir");
    //Make files for source and destination directory
    File srcDir = new File(thisDir + "/src/test/testData/example-refactorings copy");
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
  }

  //Example of using an existing repo
  //note that the project directory has to contain the .git folder but renamed
  public void testDirectory() {
    String head = repo.getCurrentRevision();

    RefactoringEntry entry = miner.getEntry(head);
    assertNotNull(entry.buildTree());

    assertNull(RefactoringEntry.fromString(""));
    assertNull(RefactoringEntry.fromString(null));

    assertEquals(1, entry.getRefactorings().size());
    assertTrue(miner.getRefactorings(head).length() > 0);

    assertThrows(IllegalArgumentException.class, () -> MiningService.getInstance(null));
    assertEquals(miner, MiningService.getInstance(myProject));
    assertNotNull(miner.getState());
    assertFalse(miner.getState().map.size() == 0);
    assertTrue(!miner.isMining());
    assertFalse(miner.getMethodHistory().isEmpty());
    assertThrows(NullPointerException.class, () -> miner.mineAndWait(null));
  }

  public void testMineAtCommit() {
    String head = repo.getCurrentRevision();
    Hash hash = new Hash() {
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

    Hash parent = mock(Hash.class);
    when(parent.asString()).thenReturn(head.substring(1));
    List<Hash> parents = Arrays.asList(parent);
    VcsUser user = new VcsUser() {
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

    VcsCommitMetadataImpl vcsCommitMetadata =
        new VcsCommitMetadataImpl(hash, parents, 0, projectRoot,
            "subject", user, "ms", user, 0);

    GitWindow gitWindow = mock(GitWindow.class);
    Mockito.doThrow(new NullPointerException()).when(gitWindow).refresh(any());
    miner.mineAtCommit(vcsCommitMetadata, myProject, gitWindow);
    verify(parent, new Times(1)).asString();
  }
}