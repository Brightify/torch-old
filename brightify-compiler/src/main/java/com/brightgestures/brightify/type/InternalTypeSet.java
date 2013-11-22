package com.brightgestures.brightify.type;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.SupportedType;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.sql.AbstractTypeAffinity;
import com.brightgestures.brightify.sql.affinity.NoneAffinity;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class InternalTypeSet {

    private InternalTypeSet() {
    }

    public static SupportedType[] getAll(TypeHelper typeHelper) {
        SupportedType[] supportedTypes = {
                KeyType.create(typeHelper)
        };

        return supportedTypes;
    }

    static abstract class AbstractInternalType implements SupportedType {
        protected final TypeHelper typeHelper;
        protected TypeMirror type;

        public AbstractInternalType(TypeHelper typeHelper, TypeMirror type) {
            this.typeHelper = typeHelper;
            this.type = type;
        }
    }

    public static class KeyType extends AbstractInternalType {

        public KeyType(TypeHelper typeHelper, TypeMirror typeMirror) {
            super(typeHelper, typeMirror);
        }

        @Override
        public boolean isSupported(Property property) {
            return typeHelper.getProcessingEnvironment().getTypeUtils().isAssignable(property.type, type);
        }

        @Override
        public void read(Property property, SourceFileGenerator sourceFileGenerator) {
            TypeMirror keyType = getKeyType(property);
            if (keyType != null) {
                sourceFileGenerator.line("entity.")
                                   .append(property.setValue("Key.keyFromByteArray(cursor.getBlob(index), " + keyType.toString() + ".class)"))
                                   .append(";")
                                   .append(" // ")
                                   .append(property.type);
            } else {
                // FIXME write better warning message
                typeHelper.getProcessingEnvironment().getMessager().printMessage(
                        Diagnostic.Kind.WARNING, "You shouldn't use Key without type because of performance!");
                sourceFileGenerator.line("entity.")
                                   .append(property.setValue("Key.keyFromByteArray(cursor.getBlob(index))"))
                                   .append(";")
                                   .append(" // ")
                                   .append(property.type);
            }
        }

        @Override
        public void write(Property property, SourceFileGenerator sourceFileGenerator) {
            sourceFileGenerator.line("values.put(\"")
                               .append(property.columnName)
                               .append("\", Key.keyToByteArray(entity.")
                               .append(property.getValue())
                               .append(")); // ")
                               .append(property.type);
        }

        @Override
        public Class<? extends AbstractTypeAffinity> getAffinity(Property property) {
            return NoneAffinity.class;
        }

        private TypeMirror getKeyType(Property property) {
            if (property.type instanceof DeclaredType) {
                DeclaredType propertyType = (DeclaredType) property.type;

                List<? extends TypeMirror> typeArguments = propertyType.getTypeArguments();
                if (typeArguments.size() == 1) {
                    return typeArguments.iterator().next();
                } else if (typeArguments.size() == 0) {
                    // FIXME write better warning message
                    typeHelper.getProcessingEnvironment().getMessager().printMessage(
                            Diagnostic.Kind.WARNING, "You shouldn't use Key without type because of performance!");
                    return null;
                } else {
                    throw new IllegalArgumentException("Illegal number of generic parameters");
                }
            } else {
                throw new IllegalArgumentException("This property is not key property!");
            }
        }

        public static KeyType create(TypeHelper typeHelper) {
            Types types = typeHelper.getProcessingEnvironment().getTypeUtils();
            Elements elements = typeHelper.getProcessingEnvironment().getElementUtils();
            TypeMirror type = types.getDeclaredType(
                    elements.getTypeElement(Key.class.getName()),
                    types.getWildcardType(null, null));

            return new KeyType(typeHelper, type);
        }
    }
}
