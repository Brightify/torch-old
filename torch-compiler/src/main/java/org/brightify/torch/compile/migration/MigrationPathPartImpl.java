package org.brightify.torch.compile.migration;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationPathPartImpl implements MigrationPathPart {

    private MigrationMethod migrationMethod;
    private MigrationPathPart previous;
    private MigrationPathPart next;

    @Override
    public MigrationMethod getMigrationMethod() {
        return migrationMethod;
    }

    public void setMigrationMethod(MigrationMethod migrationMethod) {
        this.migrationMethod = migrationMethod;
    }

    @Override
    public MigrationPathPart getPrevious() {
        return previous;
    }

    public void setPrevious(MigrationPathPart previous) {
        this.previous = previous;
    }

    @Override
    public MigrationPathPart getNext() {
        return next;
    }

    public void setNext(MigrationPathPart next) {
        this.next = next;
    }
}
