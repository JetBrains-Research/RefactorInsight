<h1> <img align="left" width="50" height="50" src="https://s3-eu-west-1.amazonaws.com/public-resources.ml-labs.aws.intellij.net/static/refactor-insight/icon.svg" alt="RefactorInsight Icon"> RefactorInsight </h1>

[![JB Research](https://jb.gg/badges/research-flat-square.svg)](https://research.jetbrains.org/)
[![pipeline status](https://github.com/JetBrains-Research/refactorinsight/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JetBrains-Research/refactorinsight/badges/master)
[![coverage report](https://codecov.io/gh/JetBrains-Research/refactorinsight/branch/master/graph/badge.svg)](https://codecov.io/gh/JetBrains-Research/refactorinsight)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/14704-refactorinsight.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

An IntelliJ IDEA plugin that discovers performed refactorings in the commit history of Java projects and visualizes them. The main framework that is used to retrieve the refactorings is [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner).

The plugin is available for download in the Marketplace: [RefactorInsight](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

## Getting started
### Refactorings in commits
In order to see the refactorings in a git repository, make sure it was cloned with full history and open the built-in `Git` tab. On the right side, you will see a toggle button ![](assets/img/refactorinsight_toggle.svg).

Click on a commit and then on the ![](assets/img/refactorinsight_toggle.svg) to see the refactorings detected in that commit. It looks like this:

<img src="assets/img/refactorinsight_main.png" alt="Example" width="900">

### Refactoring history of an object
In order to see the refactoring history of a method, class, or attribute, right-click on the object's signature and click on `Check Refactoring History`. An example for a method named `Repl` is presented below:

<img src="assets/img/refactorinsight_history.png" alt="RefactoringHistory" width="400">

If you double-click on a leaf node in a tree, the VCS log opens at that specific commit and shows a list of detected refactorings in that commit.

### Mining the refactorings

By default, only the last 100 commits are mined when you open a project. The number of commits for mining could be specified in the plugin's settings. In the `Tools` menu, you can select `Mine All Refactorings` to mine all commits in the commit history of a project.
The detected code refactorings are stored in the `refactorings.xml` file that can be found in the `.idea` folder. If the file is deleted, the commits are mined when the next commit is made.

## Got any questions?
If you have any questions about the plugin or want to report any bugs, feel free to contact us using [GitHub Issues](https://github.com/JetBrains-Research/IntelliJDeodorant/issues). 
If you want to contribute, please create pull requests.

If you want to know more about RefactoringMiner that the plugin is based on, please refer to this [paper](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2020.pdf).
