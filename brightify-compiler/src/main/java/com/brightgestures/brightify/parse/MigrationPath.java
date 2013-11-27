package com.brightgestures.brightify.parse;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class MigrationPath {

    String description;
    MigrationPathPart start;

    public MigrationPathPart getStart() {
        return start;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        if(start == null) {
            throw new NullPointerException("Start PathPart cannot be null!");
        }

        int cost = 0;
        for(MigrationPathPart part = start; part != null; part = part.next) {
            cost++;
        }

        return cost;
    }

    public int getPreferredPartsCount() {
        if(start == null) {
            throw new NullPointerException("Start PathPart cannot be null!");
        }

        int count = 0;
        for(MigrationPathPart part = start; part != null; part = part.next) {
            if(part.migrationMethod.isPreferred()) {
                count++;
            }
        }

        return count;
    }

}
