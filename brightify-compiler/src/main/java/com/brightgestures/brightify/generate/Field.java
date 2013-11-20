package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFile;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Field {

    private Protection protection = Protection.DEFAULT;
    private boolean isStatic;
    private boolean isFinal;
    private String type;
    private String name;
    private String value;

    public Protection getProtection() {
        return protection;
    }

    public Field setProtection(Protection protection) {
        this.protection = protection;
        return this;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Field setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Field setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return this;
    }

    public String getType() {
        return type;
    }

    public Field setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public Field setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Field setValue(String value) {
        this.value = value;
        return this;
    }

    public void write(SourceFile generator) {
        generator.line(protection.getValue());

        if(isStatic) {
            generator.append("static ");
        }
        if(isFinal) {
            generator.append("final ");
        }
        generator.append(type).append(" ").append(name).append(" = ").append(value).append(";");
    }

    enum Protection {
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
