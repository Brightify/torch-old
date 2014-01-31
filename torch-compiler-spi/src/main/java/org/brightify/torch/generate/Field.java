package org.brightify.torch.generate;

import org.brightify.torch.util.SourceFile;

import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Field {
    List<String> getImports();

    Protection getProtection();

    Field setProtection(Protection protection);

    boolean isStatic();

    Field setStatic(boolean isStatic);

    boolean isFinal();

    Field setFinal(boolean isFinal);

    String getTypeSimpleName();

    String getTypeFullName();

    Field setTypeFullName(String typeFullName);

    String getName();

    Field setName(String name);

    String getValue();

    Field setValue(String value);

    void write(SourceFile generator);

    public enum Protection {
        DEFAULT(""),
        PRIVATE("private "),
        PROTECTED("protected "),
        PUBLIC("public ");

        private final String value;

        Protection(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
