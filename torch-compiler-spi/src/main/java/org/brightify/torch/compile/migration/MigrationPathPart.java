package org.brightify.torch.compile.migration;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationPathPart {

    MigrationMethod getMigrationMethod();

    MigrationPathPart getNext();

    MigrationPathPart getPrevious();

}
