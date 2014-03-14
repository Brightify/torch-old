package org.brightify.torch.parse;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FieldProperty extends Property {

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String setValue(String value) {
        return getName() + " = " + value;
    }

    @Override
    public String getValue() {
        return getName();
    }
}
