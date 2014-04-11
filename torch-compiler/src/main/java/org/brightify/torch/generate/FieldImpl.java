package org.brightify.torch.generate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FieldImpl implements Field {

    protected Protection protection = Protection.DEFAULT;
    protected boolean isStatic;
    protected boolean isFinal;
    protected String typeSimpleName;
    protected String typeFullName;
    protected List<String> imports = new LinkedList<String>();
    protected String name;
    protected String value;

    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public Protection getProtection() {
        return protection;
    }

    @Override
    public Field setProtection(Protection protection) {
        this.protection = protection;
        return this;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public Field setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public Field setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return this;
    }

    @Override
    public String getTypeSimpleName() {
        return typeSimpleName;
    }

    @Override
    public String getTypeFullName() {
        return typeFullName;
    }

    @Override
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Field setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Field setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public void write(MetadataSourceFile generator) {
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

}
