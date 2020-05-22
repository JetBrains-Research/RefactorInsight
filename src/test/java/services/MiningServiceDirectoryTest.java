package services;

import data.RefactoringEntry;
import git4idea.test.GitSingleRepoTest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

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
    File srcDir = new File(thisDir + "/src/test/testData/exampleTestProject");
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
    String name = RefactoringEntry.fromString(miner.getRefactorings(repo.getCurrentRevision()))
        .getRefactorings().get(0).getName();
    assertEquals("Rename Method", name);
  }
}