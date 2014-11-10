package org.brightify.torch.compile.migration;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationMethodImpl implements MigrationMethod {

    private ExecutableElement executable;
    private boolean preferred;
    private long sourceRevision;
    private long targetRevision;

    @Override
    public ExecutableElement getExecutable() {
        return executable;
    }

    public void setExecutable(ExecutableElement executable) {
        this.executable = executable;
    }

    @Override
    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    @Override
    public long sourceRevision() {
        return sourceRevision;
    }

    public void setSourceRevision(long sourceRevision) {
        this.sourceRevision = sourceRevision;
    }

    @Override
    public long targetRevision() {
        return targetRevision;
    }

    public void setTargetRevision(long targetRevision) {
        this.targetRevision = targetRevision;
    }
}
