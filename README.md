<h1> <img align="left" width="50" height="50" src="https://s3-eu-west-1.amazonaws.com/public-resources.ml-labs.aws.intellij.net/static/refactor-insight/icon.svg" alt="RefactorInsight Icon"> RefactorInsight </h1>

[![JB Research](https://jb.gg/badges/research-flat-square.svg)](https://research.jetbrains.org/)
[![pipeline status](https://github.com/JetBrains-Research/refactorinsight/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JetBrains-Research/refactorinsight/badges/master)
[![coverage report](https://codecov.io/gh/JetBrains-Research/refactorinsight/branch/master/graph/badge.svg)](https://codecov.io/gh/JetBrains-Research/refactorinsight)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/14704-refactorinsight.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

RefactorInsight is a plugin for IntelliJ IDEA that enables the IDE to visualize refactorings in the commit history of Java projects. RefactorInsight relies on [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner) for retrieval of refactorings.

## Getting started
RefactorInsight is сompatible with IntelliJ IDEA 2020.1 and is available for free in the Marketplace: [RefactorInsight](https://plugins.jetbrains.com/plugin/14704-refactorinsight). 

### Refactorings in commits
To see the refactorings in a git repository, open the built-in `Git` tab. On the right side, you will see a toggle button ![](assets/img/refactorinsight_toggle.svg).

Click on a commit and then on the ![](assets/img/refactorinsight_toggle.svg) to see the refactorings detected in that commit. It looks like this:

<img src="assets/img/refactorinsight_main.png" alt="Example" width="900">

### Refactoring history of an object
In order to see the refactoring history of a method, class, or attribute, right-click on the object's signature and select `Check Refactoring History`. Here is an example for a method named `Repl`:

<img src="assets/img/refactorinsight_history.png" alt="RefactoringHistory" width="400">

If you double-click on a leaf node in a tree, the VCS log opens at that specific commit and shows a list of detected refactorings in that commit.

### Settings
By default, for performance reasons, refactorings are only retrieved from the last 100 commits in history. This number can be adjusted in the plugin's settings. Alternatively, you can use `Mine All Refactorings` action in the `Tools` menu to go through all commits in the history of your project.
The detected code refactorings are stored in `.idea/refactorings.xml`. If this file is deleted, RefactorInsight will mine refactorings again once you make a commit.


## Contribution and feedback
To submit a bug report or suggest a feature, please [open an issue](https://github.com/JetBrains-Research/refactorinsight/issues). 
Pull requests are welcome and encouraged.


[This paper](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2020.pdf) provides a detailed overview of RefactoringMiner — the tool RefactorInsight relies on to identify refactorings.
