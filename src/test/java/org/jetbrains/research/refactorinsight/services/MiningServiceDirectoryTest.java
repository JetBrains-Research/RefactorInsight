package org.jetbrains.research.refactorinsight.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsUser;
import com.intellij.vcs.log.impl.VcsCommitMetadataImpl;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import git4idea.test.GitSingleRepoTest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.jetbrains.research.refactorinsight.ui.tree.TreeUtils;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.HistoryToolbarRenderer;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.MainCellRenderer;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;

/**
 * Extend GitSingleRepoTest
 * variables as myProject, repo, projectPath and much more are available from super classes
 * test method names have to begin with test!
 */
public class MiningServiceDirectoryTest extends GitSingleRepoTest {

  private MiningService miner;
  private String head;
  private RefactoringEntry entry;

  //does not have to be overwritten,
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
    entry = miner.get(head);
  }

  /**
   * This test method is used for testing multiple functionalities.
   * This is the case since the setup method can be heavy and there is no need
   * in mining multiple times.
   * This method test the functionality of the MiningService (miner in this case).
   * It tests the miner at commit method using mocking.
   * It also tests the renderers.
   */
  public void testDirectory() {

    //Testing the HEAD entry size:
    assertEquals(3, entry.getRefactorings().size());
    assertTrue(miner.get(head).getRefactorings().size() > 0);

    //Testing that the mining service does not accept null as project:
    assertThrows(IllegalArgumentException.class, () -> MiningService.getInstance(null));

    //Testing that the current project has a mining service:
    assertEquals(miner, MiningService.getInstance(myProject));
    assertNotNull(miner.getState());
    assertFalse(miner.getState().refactoringsMap.map.isEmpty());

    //Testing that the miner has finished mining:
    assertTrue(!miner.isMining());

    //Testing that the refactoring history is computed:
    assertFalse(miner.getRefactoringHistory().isEmpty());

    //Testing that the mining cannot happen on a null repository:
    assertThrows(NullPointerException.class, () -> miner.mineAndWait(null));

    //test mine at commit
    Hash hash = createHashObject(head);
    Hash parent = mock(Hash.class);
    //return a random string
    when(parent.asString()).thenReturn("");
    List<Hash> parents = Arrays.asList(parent);
    VcsUser user = createTestVcsUser();

    //cannot be mocked since its dependencies must not be null
    VcsCommitMetadataImpl vcsCommitMetadata = new VcsCommitMetadataImpl(hash,
        parents, 0, repo.getRoot(), "subject",
        user, "message", user, 0);

    GitWindow gitWindow = mock(GitWindow.class);
    Mockito.doThrow(new NullPointerException()).when(gitWindow).refresh(any());

    //Testing that mine at commit works:
    miner.mineAtCommit(vcsCommitMetadata, myProject, gitWindow);
    verify(parent, new Times(2)).asString();

    //test the main cell renderer works as expected on this map
    MainCellRenderer cellRenderer = new MainCellRenderer();
    miner.getState().refactoringsMap.map.values()
        .forEach(x -> {
          Tree tree1 = TreeUtils.buildTree(x.getRefactorings());
          tree1.setCellRenderer(cellRenderer);
          //Testing that the VCS tool tree is not null:
          assertNotNull(tree1);
          DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) tree1.getModel().getRoot();

          //for each refactoring check that the renderer works properly
          root1.breadthFirstEnumeration().asIterator().forEachRemaining(node -> {
            cellRenderer.customizeCellRenderer(tree1, node, false,
                false, node.isLeaf(), 1, false);
            //Testing that for node "node" the renderer works
            assertNotNull(cellRenderer
                .getTreeCellRendererComponent(tree1, node, false,
                    false, node.isLeaf(), 1, false));
          });
        });

    //test the history toolbar renderer works as expected on this map
    HistoryToolbarRenderer historyToolbarRenderer = new HistoryToolbarRenderer();
    miner.getRefactoringHistory()
        .forEach((key, refactorings) -> {
          DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
          for (RefactoringInfo ref : refactorings) {
            TreeUtils.createHistoryTree(root, ref);
          }
          Tree tree1 = new Tree(root);
          tree1.setCellRenderer(historyToolbarRenderer);
          //testing that the history toolbar tree is not null
          assertNotNull(tree1);
          DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) tree1.getModel().getRoot();
          root1.breadthFirstEnumeration().asIterator().forEachRemaining(node -> {
            historyToolbarRenderer.customizeCellRenderer(tree1, node, false,
                false, node.isLeaf(), 1, false);
            //Testing that for node "node" the renderer works"
            assertNotNull(historyToolbarRenderer
                .getTreeCellRendererComponent(tree1, node, false,
                    false, node.isLeaf(), 1, false));
          });
        });
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