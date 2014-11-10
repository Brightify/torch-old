package org.brightify.torch.parse;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationMethod {

    private final ExecutableElement executable;
    private final boolean preferred;
    private final long sourceRevision;
    private final long targetRevision;

    public MigrationMethod(ExecutableElement executable, boolean preferred, long sourceRevision, long targetRevision) {
        this.executable = executable;
        this.preferred = preferred;
        this.sourceRevision = sourceRevision;
        this.targetRevision = targetRevision;
    }

    public ExecutableElement getExecutable() {
        return executable;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public long getSourceRevision() {
        return sourceRevision;
    }

    public long getTargetRevision() {
        return targetRevision;
    }
}
