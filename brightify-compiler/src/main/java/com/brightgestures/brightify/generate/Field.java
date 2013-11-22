package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFile;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Field {

    protected Protection protection = Protection.DEFAULT;
    protected boolean isStatic;
    protected boolean isFinal;
    protected String typeSimpleName;
    protected String typeFullName;
    protected List<String> imports = new LinkedList<>();
    protected String name;
    protected String value;

    public List<String> getImports() {
        return imports;
    }

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

    public String getTypeSimpleName() {
        return typeSimpleName;
    }

    public String getTypeFullName() {
        return typeFullName;
    }

    public Field setTypeFullName(String typeFullName) {
        this.typeFullName = typeFullName;
        imports.clear();
        if(typeFullName.indexOf('.') != -1) {
            imports.add(typeFullName);
            typeSimpleName = typeFullName.substring(typeFullName.lastIndexOf('.') + 1);
        } else {
            typeSimpleName = typeFullName;
        }
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
        generator.append(typeSimpleName).append(" ").append(name).append(" = ").append(value).append(";");
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
