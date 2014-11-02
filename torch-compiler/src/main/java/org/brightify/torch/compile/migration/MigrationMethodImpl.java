package org.brightify.torch.compile.migration;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MigrationMethodImpl implements MigrationMethod{

    private ExecutableElement executable;
    private boolean preferred;
    private String fromVersion;
    private String toVersion;

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
    public String fromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    @Override
    public String toVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }
}
