package org.brightify.torch.generate;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericField extends FieldImpl {

    protected final Field[] typeArguments;
    protected String erasuredType;

    public GenericField(Field... typeArguments) {
        this.typeArguments = typeArguments;
    }

    @Override
    public GenericField setTypeFullName(String typeFullName) {
        super.setTypeFullName(typeFullName); // ArrayListMarshaller
        erasuredType = typeSimpleName;
        if(typeArguments != null && typeArguments.length > 0) {
            typeSimpleName += "<";
            int i = 0;
            for(Field typeArgument : typeArguments) {
                if(i > 0) {
                    typeSimpleName += ", ";
                }
                typeSimpleName += typeArgument.getTypeSimpleName();
                i++;
                for(String importName : typeArgument.getImports()) {
                    imports.add(importName);
                }
            }
            typeSimpleName += ">";
        }

        return this;
    }

    public String getErasuredType() {
        return erasuredType;
    }
}
