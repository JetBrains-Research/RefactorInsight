# Detecting Refactorings in Code Changes

[![pipeline status](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/badges/master/pipeline.svg)](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/badges/master)
[![coverage report](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/badges/master/coverage.svg)](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/badges/master)

[Download](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/-/jobs/artifacts/master/raw/build/distributions/detecting-refactorings-in-code-changes-1.0-SNAPSHOT.zip?job=build)


This project aims to introduce a plugin for IntelliJ IDEA that detects code refactorings in the version control history of Java repositories. 
The main framework that is used in order to retrieve the refactorings is the RefactoringMiner API.

Every sprint planning and retrospective can be found in the [Milestones section](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2019-2020-q4/cluster-0/detecting-refactorings-in-code-changes/detecting-refactorings-in-code-changes/-/milestones?sort=due_date_desc&state=all). 

The CI/CD pipelines have 3 stages: build, validate and coverage.

In the `build` stage, the project and its dependencies are built. This is followed by the `validation` stage where 
`checkstyle`, and `test` jobs are run. Finally, the `coverage` is calculated for the entire project,
excluding the package `ui.windows`. The reason is that the ui objects cannot be tested, since they have many dependencies
that cannot be mocked.

If any stage fails, the badge will turn red.

How to build & run
-------------

Download or clone the project in IntelliJ IDEA 2020.1 or up and run `gradle build` in order to install all the required dependencies.
Use `gradle runIde` task to run the project. 
A new IDE will open up where you can follow the steps below.
If you want to import the plugin from disk, run `gradle buildPlugin`. A zip will be generated in the 
build/distribution file of the repository. Go to IntelliJ -> Preferences -> Plugin -> Install Plugin
from Disk and upload the zip file. After an IDE restart, you will be able to follow the steps below.

In order to see the refactorings in a git repository, make sure you cloned the repository and 
open the built-in Git tab. On the right side, you will see a button as highlighted below.

<img src="assets/img/refactorings.png" alt="VcsLogUI" width="300">

Click on a commit and then on the Show Refactorings button presented above to see the refactorings detected at that commit. 
This should look as below.

<img src="assets/img/example.png" alt="Example" width="900">

In order to see the refactoring history of a method, right-click on the method signature as below.

<img src="assets/img/refactoring_history.png" alt="RefactoringHistory" width="400">

Click on 'Check Refactoring History' in order to see the refactorings for that method.

In the main Tools menu, you can select Mine All Refactorings if the commits have not been mined.

<img src="assets/img/toolbar.png" alt="Tools" width="300">





