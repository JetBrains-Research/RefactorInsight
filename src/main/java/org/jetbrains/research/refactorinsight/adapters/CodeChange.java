package org.jetbrains.research.refactorinsight.adapters;

import gr.uom.java.xmi.LocationInfo;

/**
 * Represents the code change of the element made in the corresponding commit.
 */
public class CodeChange {
    private final String commitId;
    private final String changeType;
    private final String description;
    private final gr.uom.java.xmi.LocationInfo locationInfoBefore;
    private final gr.uom.java.xmi.LocationInfo locationInfoAfter;

    public CodeChange(String commitId,
                      String changeType,
                      String description,
                      gr.uom.java.xmi.LocationInfo locationInfo,
                      gr.uom.java.xmi.LocationInfo locationInfoAfter) {
        this.commitId = commitId;
        this.changeType = changeType;
        this.description = description;
        this.locationInfoBefore = locationInfo;
        this.locationInfoAfter = locationInfoAfter;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }

    public LocationInfo getLocationInfoBefore() {
        return locationInfoBefore;
    }

    public LocationInfo getLocationInfoAfter() {
        return locationInfoAfter;
    }

    @Override
    public String toString() {
        return changeType;
    }

}
