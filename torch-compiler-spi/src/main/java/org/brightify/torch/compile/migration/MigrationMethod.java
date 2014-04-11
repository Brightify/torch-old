package org.brightify.torch.compile.migration;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MigrationMethod {

    ExecutableElement getExecutable();

    boolean isPreferred();

    String fromVersion();

    String toVersion();

}
