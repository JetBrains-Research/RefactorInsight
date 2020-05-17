package services;

import com.intellij.openapi.vcs.Executor;
import data.RefactoringEntry;
import git4idea.test.GitExecutor;
import git4idea.test.GitSingleRepoTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

/**
 * Extend GitSingleRepoTest
 * variables as myProject, repo, projectPath and much more are available from super classes
 * test method names have to begin with test!
 */
public class MiningServiceTest extends GitSingleRepoTest {

  //doesnt have to be overwritten,
  // but if we want to setup stuff ourselves call super.setup() first
  //No @Before annotation
  public void setUp() throws Exception {
    super.setUp();
  }

  //No @Test annotation
  //Example of creating a repo manually
  public void testManual() throws IOException {
    //prints the path to the temporaray project files
    System.out.println(projectPath);
    //Reads a file into a string.
    //These relative paths are this project
    String fileContent =
        Files.readString(Paths.get("src/test/testData/miningService/rename_method_before.java"));

    //makes a file inside the temporary project
    //All relative paths for executor are placed inside the project structure of the sandbox ide
    File file = Executor.touch("src/testclass.java", fileContent);

    //Add file like "git add ."
    //and commit with message
    //returns commit hash!
    String something = GitExecutor.addCommit(repo, "first commit");

    String fileContent1 =
        Files.readString(Paths.get("src/test/testData/miningService/rename_method_after.java"));

    //Overwrites existing file (also possible with path)
    Executor.overwrite(file, fileContent1);

    String somethingElse = GitExecutor.addCommit(repo, "second commit");
    repo.update();

    MiningService miner = myProject.getService(MiningService.class);
    miner.loaded();
    miner.mineRepo(repo);

    //wait for miner to finish mining
    //this is busy waiting which floods your cpu
    //so when mining bigger repos use thread.sleep or somthing
    while (miner.isMining()) {
    }

    String refactoring = miner.getRefactorings(somethingElse);
    String type = RefactoringEntry.fromString(refactoring).getRefactorings().get(0).getName();
    assertEquals("Rename Method", type);
  }

  //Example of using an existing repo
  //note that the project directory has to contain the .git folder but renamed
  public void testDirectory() throws IOException {
    System.out.println(projectPath);
    //get path of refactoring project
    String thisDir = System.getProperty("user.dir");

    //Make files for source and destination directory
    File srcDir = new File(thisDir + "/src/test/testData/exampleTestProject");
    File destDir = new File(projectPath);
    //Copy directory + contents
    FileUtils.copyDirectory(srcDir, destDir);

    //rename gitdir to .git
    File oldGitDir = new File(projectPath + "/gitdir");
    File newGitDir = new File(projectPath + "/.git");
    FileUtils.deleteDirectory(newGitDir);
    oldGitDir.renameTo(newGitDir);
    repo.update();

    MiningService miner = myProject.getService(MiningService.class);
    miner.loaded();
    miner.mineRepo(repo);

    while (miner.isMining()) {
    }

    String name = RefactoringEntry.fromString(miner.getRefactorings(repo.getCurrentRevision()))
        .getRefactorings().get(0).getName();

    assertEquals("Rename Method", name);
  }
}