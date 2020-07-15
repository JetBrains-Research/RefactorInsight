<h1> <img align="left" width="50" height="50" src="https://s3-eu-west-1.amazonaws.com/public-resources.ml-labs.aws.intellij.net/static/refactor-insight/icon.svg" alt="RefactorInsight Icon"> RefactorInsight </h1>

[![JB Research](https://jb.gg/badges/research-flat-square.svg)](https://research.jetbrains.org/)
[![pipeline status](https://github.com/JetBrains-Research/refactorinsight/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JetBrains-Research/refactorinsight/badges/master)
[![coverage report](https://codecov.io/gh/JetBrains-Research/refactorinsight/branch/master/graph/badge.svg)](https://codecov.io/gh/JetBrains-Research/refactorinsight)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/14704-refactorinsight.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

An IntelliJ IDEA plugin that discovers performed refactorings in the commit history of the Java projects and visualizes them. The main framework that is used in order to retrieve the refactorings is [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner).

The plugin is available for download in the Marketplace: [RefactorInsight](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

## Getting started

In order to see the refactorings in a git repository, make sure you cloned the repository and 
open the built-in Git tab. On the right side, you will see a toggle button as highlighted below.

<img src="assets/img/refactorings.png" alt="VcsLogUI" width="300">


Click on a commit and then on the `Show Refactorings` button presented above to see the refactorings detected at that commit. 
This should look as below.

<img src="assets/img/example.png" alt="Example" width="900">


In order to see the refactoring history of a method, class or attribute,
 right-click on the object's signature as below.

<img src="assets/img/refactoring_history.png" alt="RefactoringHistory" width="400">


Click on `Check Refactoring History` in order to see the refactorings history.
An example for a method named `Repl` is presented below.

<img src="assets/img/history_example.png" alt="RefactoringHistory2" width="400">


If you double-click on a leaf node in a tree, the VCS log opens at that specific commit.

<img src="assets/img/history_example2.png" alt="RefactoringHistory3" width="900">


In the main `Tools` menu, you can select `Mine All Refactorings` if the commits have not been mined.

<img src="assets/img/toolbar.png" alt="Tools" width="200">


The detected code refactorings are stored and persisted in the `refactorings.xml` file that can be found in the
`.idea` folder. If the file is deleted, the commits are mined with the next repository changed event.

## Got any questions?
If you have any questions about the plugin or want to report any bugs, feel free to contact us using [GitHub Issues](https://github.com/JetBrains-Research/refactorinsight/issues). 

If you want to know more about RefactoringMiner that the plugin is based on, please refer to this [paper](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2020.pdf).
