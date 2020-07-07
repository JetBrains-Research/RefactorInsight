# RefactorInsight

[![JB Research](https://jb.gg/badges/research-flat-square.svg)](https://research.jetbrains.org/)
[![pipeline status](https://github.com/JetBrains-Research/refactorinsight/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JetBrains-Research/refactorinsight/badges/master)
[![coverage report](https://codecov.io/gh/JetBrains-Research/refactorinsight/branch/master/graph/badge.svg)](https://codecov.io/gh/JetBrains-Research/refactorinsight)



This project aims to introduce a RefactorInsight, a plugin for IntelliJ IDEA that detects code refactorings in the version control history of Java repositories. 
The main framework that is used in order to retrieve the refactorings is the RefactoringMiner API.


How to build & run
-------------

#### Clone and build

Download or clone the project in IntelliJ IDEA 2020.1 or up and run `gradle build` in order to install all the required dependencies.
Use `gradle runIde` task to run the project. 
A new IDE will open up where you can follow the steps presented below.

#### Import from disk 

If you want to install the plugin from disk, download the latest release.


Go to IntelliJ -> Preferences -> Plugin -> Install Plugin
from Disk and upload the zip file. After an IDE restart, you will be able to follow the steps below.

#### How to use

In order to see the refactorings in a git repository, make sure you cloned the repository and 
open the built-in Git tab. On the right side, you will see a button as highlighted below.

<img src="assets/img/refactorings.png" alt="VcsLogUI" width="300">


Click on a commit and then on the Show Refactorings button presented above to see the refactorings detected at that commit. 
This should look as below.

<img src="assets/img/example.png" alt="Example" width="900">


In order to see the refactoring history of a method, class or attribute,
 right-click on the object's signature as below.

<img src="assets/img/refactoring_history.png" alt="RefactoringHistory" width="400">


Click on 'Check Refactoring History' in order to see the refactorings history.
An example for a class `LuxuryCar` is presented below.

<img src="assets/img/history_example.png" alt="RefactoringHistory2" width="400">


If you double-click on a leaf node in a tree, the VCS log opens at that specific commit.

<img src="assets/img/history_example2.png" alt="RefactoringHistory3" width="900">


In the main Tools menu, you can select Mine All Refactorings if the commits have not been mined.

<img src="assets/img/toolbar.png" alt="Tools" width="200">


The detected code refactorings are stored and persisted in the `refactorings.xml` file that can be found in the
`.idea` folder. If the file is deleted, the commits are mined with the next repository changed event.




