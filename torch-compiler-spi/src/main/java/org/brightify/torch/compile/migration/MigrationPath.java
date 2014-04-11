package org.brightify.torch.compile.migration;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationPath {

    MigrationPathPart getStart();

    String getDescription();

    int getCost();

    int getPreferredPartsCount();

}
