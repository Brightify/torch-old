package com.brightgestures.brightify.parse;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public class MigrationPathPart {
    MigrationMethod migrationMethod;
    MigrationPathPart previous;
    MigrationPathPart next;

    public MigrationMethod getMigrationMethod() {
        return migrationMethod;
    }

    public MigrationPathPart getNext() {
        return next;
    }

    public MigrationPathPart getPrevious() {
        return previous;
    }
}
