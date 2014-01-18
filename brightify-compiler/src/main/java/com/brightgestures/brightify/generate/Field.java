package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Field {

    protected Protection protection = Protection.DEFAULT;
    protected boolean isStatic;
    protected boolean isFinal;
    protected String typeSimpleName;
    protected String typeFullName;
    protected List<String> imports = new LinkedList<String>();
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


/*
        GenericType result;

        GenericType currentDeclaration;
        int current = 0;
        int level = 0;
        int last = typeFullName.length();
        do {

            currentDeclaration = new GenericType();



        } while (current < last);
*/
        this.typeFullName = typeFullName;
        imports.clear();
        if (typeFullName.indexOf('.') != -1) {
            imports.add(typeFullName);
            typeSimpleName = typeFullName.substring(typeFullName.lastIndexOf('.') + 1);
        } else {
            typeSimpleName = typeFullName;
        }
        return this;
    }

    private GenericType parse(String name) {
        if(name.length() == 0) {
            return null;
        }

        GenericType currentType = null;
        StringBuilder builder = new StringBuilder();
        int level = 0;
        Map<
            Map<
                Map<
                    String,
                    String
                >,
                Map<
                    String,
                    String
                >
            >,
            List<
                Map<
                    String,
                    String
                >
            >
        > a;

        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if(c == '<') {
                level++;
                GenericType genericType = new GenericType();
                genericType.fullName = builder.toString();
                genericType.parent = currentType;

                if(currentType != null) {
                    currentType.children.add(genericType);
                }
                currentType = genericType;
                builder = new StringBuilder();
            } else if(c == ',') {
                GenericType genericType = new GenericType();
                genericType.parent = currentType;
                genericType.fullName = builder.toString();
                currentType.children.add(genericType);
                builder = new StringBuilder();
            } else if(c == '>') {
                level--;
                GenericType genericType = new GenericType();
                genericType.fullName = builder.toString();
                genericType.parent = currentType;
                currentType.children.add(genericType);
                currentType = currentType.parent;
                builder = new StringBuilder();
            } else if(c != ' ') {
                builder.append(c);
            }
        }
/*

        int nextBracket = name.indexOf("<");
        int nextColon = name.indexOf(",");

        GenericType type = new GenericType();

        if (nextBracket == -1 && nextColon == -1) {
            type.fullName = name;
            type.name = type.fullName.substring(type.fullName.lastIndexOf('.') + 1);
        }

        if (nextBracket != -1 && nextBracket < nextColon) {

        } else if (nextColon != -1 && nextBracket) {

        }*/

        return null;
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

        if (isStatic) {
            generator.append("static ");
        }
        if (isFinal) {
            generator.append("final ");
        }
        generator.append(typeSimpleName).append(" ").append(name).append(" = ").append(value).append(";");
    }

    class GenericType {
        GenericType parent;
        List<GenericType> children = new ArrayList<GenericType>();
        String name;
        String fullName;
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
