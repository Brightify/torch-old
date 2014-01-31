package org.brightify.torch.parse;

import org.brightify.torch.util.VersionComparator;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class MigrationMethod {

    private final ExecutableElement executable;
    private final boolean preferred;
    private final String fromVersion;
    private final String toVersion;

    public MigrationMethod(ExecutableElement executable, boolean preferred, String fromVersion, String toVersion) {
        this.executable = executable;
        this.preferred = preferred;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    public ExecutableElement getExecutable() {
        return executable;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public static int compare(String v1, String v2) {
        VersionComparator versionComparator = new VersionComparator();

        return versionComparator.compare(v1, v2);
    }
}
