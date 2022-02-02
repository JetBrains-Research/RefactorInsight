package org.jetbrains.research.refactorinsight.adapters;

import gr.uom.java.xmi.LocationInfo;
import org.codetracker.change.Change.Type;
import org.jetbrains.research.refactorinsight.ui.windows.ElementType;

/**
 * Represents the code change of the element made in the corresponding commit.
 */
public class CodeChange {
    private final String commitId;
    private final Type changeType;
    private final String description;
    private final String changeDate;
    private final String changeAuthor;
    private final gr.uom.java.xmi.LocationInfo locationInfoBefore;
    private final gr.uom.java.xmi.LocationInfo locationInfoAfter;
    private final ElementType type;

    public CodeChange(String commitId,
                      Type changeType,
                      String description,
                      String changeDate,
                      String changeAuthor,
                      gr.uom.java.xmi.LocationInfo locationInfo,
                      gr.uom.java.xmi.LocationInfo locationInfoAfter,
                      ElementType type) {
        this.commitId = commitId;
        this.changeType = changeType;
        this.description = description;
        this.changeDate = changeDate;
        this.changeAuthor = changeAuthor;
        this.locationInfoBefore = locationInfo;
        this.locationInfoAfter = locationInfoAfter;
        this.type = type;
    }

    public String getCommitId() {
        return commitId;
    }

    public Type getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public String getChangeAuthor() {
        return changeAuthor;
    }

    public ElementType getType() {
        return type;
    }

    public LocationInfo getLocationInfoBefore() {
        return locationInfoBefore;
    }

    public LocationInfo getLocationInfoAfter() {
        return locationInfoAfter;
    }

    @Override
    public String toString() {
        return changeType.getTitle();
    }

}
