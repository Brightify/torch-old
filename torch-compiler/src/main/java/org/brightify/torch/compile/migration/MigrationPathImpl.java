package org.brightify.torch.compile.migration;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationPathImpl implements MigrationPath {

    private MigrationPathPart start;
    private String description;
    private int cost;
    private int preferredPartsCount;

    @Override
    public MigrationPathPart getStart() {
        return start;
    }

    public void setStart(MigrationPathPart start) {
        this.start = start;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public int getPreferredPartsCount() {
        return preferredPartsCount;
    }

    public void setPreferredPartsCount(int preferredPartsCount) {
        this.preferredPartsCount = preferredPartsCount;
    }
}
