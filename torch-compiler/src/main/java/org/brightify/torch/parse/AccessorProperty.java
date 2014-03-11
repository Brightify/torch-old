package org.brightify.torch.parse;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AccessorProperty extends Property {

    private String getterName;
    private String getterFullName;
    private String setterName;
    private String setterFullName;
    private boolean forced;

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    public String getGetterFullName() {
        return getterFullName;
    }

    public void setGetterFullName(String getterFullName) {
        this.getterFullName = getterFullName;
    }

    public String getSetterName() {
        return setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public String getSetterFullName() {
        return setterFullName;
    }

    public void setSetterFullName(String setterFullName) {
        this.setterFullName = setterFullName;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    @Override
    public String setValue(String value) {
        return setterName + "(" + value + ")";
    }

    @Override
    public String getValue() {
        return getterName + "()";
    }
}
