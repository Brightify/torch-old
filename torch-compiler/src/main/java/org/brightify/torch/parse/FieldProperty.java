package org.brightify.torch.parse;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FieldProperty extends Property {

    private String name;
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String setValue(String value) {
        return name + " = " + value;
    }

    @Override
    public String getValue() {
        return name;
    }
}
