package com.brightgestures.brightify.parse;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class MigrationMethod {

    private final ExecutableElement executable;
    private final int fromVersion;
    private final int toVersion;

    public MigrationMethod(ExecutableElement executable, int fromVersion, int toVersion) {
        this.executable = executable;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    public ExecutableElement getExecutable() {
        return executable;
    }

    public int getFromVersion() {
        return fromVersion;
    }

    public int getToVersion() {
        return toVersion;
    }
}
