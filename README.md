<h1> <img align="left" width="50" height="50" src="https://s3-eu-west-1.amazonaws.com/public-resources.ml-labs.aws.intellij.net/static/refactor-insight/icon.svg" alt="RefactorInsight Icon"> RefactorInsight </h1>

[![JB Research](https://jb.gg/badges/research-flat-square.svg)](https://research.jetbrains.org/)
[![pipeline status](https://github.com/JetBrains-Research/refactorinsight/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JetBrains-Research/refactorinsight/badges/master)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/14704-refactorinsight.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/14704-refactorinsight)

RefactorInsight is a plugin for IntelliJ IDEA that enhances the representation of code changes with refactoring information in Java and Kotlin projects. RefactorInsight relies on [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner) and [kotlinRMiner](https://github.com/JetBrains-Research/kotlinRMiner) for retrieval of refactorings.

## Overview
RefactorInsight is сompatible with IntelliJ IDEA 2022.2 and is [available for free](https://plugins.jetbrains.com/plugin/14704-refactorinsight) on JetBrains Marketplace.

### Smart diff
To make reviewing bug fixes or new features easier, the plugin auto folds refactorings in code diffs and shows hints with short descriptions of each refactoring.
Currently, it works for the following refactoring types: `Move Method`, `Pull Up/Push Down Method`, `Extract Method`, and `Inline Method`.

<img src="assets/img/refactorinsight_hint.png" alt="Example" width="600">

### Refactorings in commits
To see refactorings in your Git repository, go to the **Git** tab in IntelliJ IDEA and click the toggle button ![](assets/img/refactorinsight_toggle.svg) on the right.

Click a commit and then the ![](assets/img/refactorinsight_toggle.svg) to see the refactorings detected in that commit. It looks like this:

<img src="assets/img/refactorinsight_main.png" alt="Example" width="900">

### Refactorings in pull requests
To see the refactorings in a specific pull request, go to the **Pull Requests** tab in IntelliJ IDEA, select any pull request and click ![](assets/img/refactorinsight_toggle.svg).

<img src="assets/img/refactorinsight_pull_requests.png" alt="PullRequests" width="600">

### Refactoring history of an object
To see the refactoring history of a method, class, or attribute, right-click the object's signature and select **Check Refactoring History**. Here is an example for a method named `Repl`:

<img src="assets/img/refactorinsight_history.png" alt="RefactoringHistory" width="400">

Double-click a leaf node in the tree to open the VCS log at that specific commit and see a list of detected refactorings in that commit.

### Settings
By default, for performance reasons, refactorings are only retrieved from the last 100 commits in the project history. This number can be adjusted in the plugin's settings. Alternatively, you can use **Mine All Refactorings** on the **Tools** menu to go through _all_ commits in the history of your project.
The plugin also keeps track of new commits and processes them.
The detected code refactorings are stored in `.idea/refactorings.xml`. If this file is deleted, RefactorInsight mines refactorings again after you make a commit.


## Contribution and feedback
You are welcome to submit a bug report or suggest a feature: [open an issue](https://github.com/JetBrains-Research/refactorinsight/issues). 
Pull requests are also welcome and encouraged.


For more information about the tool on which RefactorInsight relies to identify refactorings, read [RefactoringMiner 2.0](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2020.pdf).

## Citing RefactorInsight
A [paper](https://arxiv.org/abs/2108.11202) about RefactorInsight was presented at [ASE'21](https://conf.researchr.org/home/ase-2021). 
If you use RefactorInsight in your academic work, please cite it.
```
@article{kurbatova2021refactorinsight,
  title={Refactorinsight: Enhancing ide representation of changes in git with refactorings information},
  author={Kurbatova, Zarina and Kovalenko, Vladimir and Savu, Ioana and Brockbernd, Bob and Andreescu, Dan 
  and Anton, Matei and Venediktov, Roman and Tikhomirova, Elena and Bryksin, Timofey},
  journal={arXiv preprint arXiv:2108.11202},
  year={2021}
}
```
