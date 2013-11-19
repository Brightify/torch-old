package com.brightgestures.brightify.type;

import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.SupportedType;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.sql.TypeName;
import com.brightgestures.brightify.sql.affinity.NoneAffinity;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class CollectionTypeSet {

    private CollectionTypeSet() {
    }

    public static SupportedType[] getAll(TypeHelper typeHelper) {

        return new SupportedType[] {
                new ListType(typeHelper)
        };
    }

    public static class ListType implements SupportedType {

        private final TypeHelper typeHelper;

        public ListType(TypeHelper typeHelper) {
            this.typeHelper = typeHelper;
        }

        @Override
        public boolean isSupported(Property property) {
            return typeHelper.getProcessingEnvironment().getTypeUtils().isAssignable(
                    typeHelper.getProcessingEnvironment().getTypeUtils().erasure(property.type), typeHelper.typeOf(
                    List.class));
        }

        @Override
        public void read(Property property, SourceFileGenerator sourceFileGenerator) {
            DeclaredType propertyType = (DeclaredType) property.type;
            TypeMirror itemType = propertyType.getTypeArguments().iterator().next();
            if (typeHelper.getProcessingEnvironment().getTypeUtils().isSameType(
                    typeHelper.getProcessingEnvironment().getTypeUtils().erasure(propertyType),
                    typeHelper.getProcessingEnvironment().getTypeUtils().erasure(typeHelper.typeOf(List.class)))) {
                propertyType = typeHelper.getProcessingEnvironment().getTypeUtils().getDeclaredType(
                        typeHelper.getProcessingEnvironment().getElementUtils().getTypeElement(
                                ArrayList.class.getName()), itemType);
            }

            sourceFileGenerator.line(
                    "java.io.DataInputStream inputStream = new java.io.DataInputStream(new java.io" +
                    ".ByteArrayInputStream(cursor.getBlob(index)));");
            sourceFileGenerator.line(propertyType).append(" list = new ").append(propertyType).append("();");
            sourceFileGenerator.line(
                    "com.brightgestures.brightify.marshall.CollectionMarshaller.unmarshall(inputStream, list, " +
                    "com.brightgestures.brightify.marshall.GenericMarshallers.get(")
                               .append(itemType).append(".class));");
            sourceFileGenerator.line("entity.").append(property.setValue("list")).append("; // ").append(property.type);
        }

        @Override
        public void write(Property property, SourceFileGenerator sourceFileGenerator) {
            DeclaredType propertyType = (DeclaredType) property.type;
            TypeMirror itemType = propertyType.getTypeArguments().iterator().next();
            TypeMirror itemTypeErasure = typeHelper.getProcessingEnvironment().getTypeUtils().erasure(itemType);

            sourceFileGenerator.line(
                    "java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();");
            sourceFileGenerator.line(
                    "java.io.DataOutputStream outputStream = new java.io.DataOutputStream(byteArrayOutputStream);");
            sourceFileGenerator.line(
                    "com.brightgestures.brightify.marshall.CollectionMarshaller.marshall(outputStream, entity.")
                               .append(property.getValue())
                               .append(", com.brightgestures.brightify.marshall.GenericMarshallers.get(")
                               .append(itemTypeErasure)
                               .append(".class));");
            sourceFileGenerator.line("outputStream.flush();");
            sourceFileGenerator.line("values.put(\"")
                               .append(property.columnName)
                               .append("\", byteArrayOutputStream.toByteArray()); // ")
                               .append(property.type);
        }

        @Override
        public Class<? extends TypeName> getAffinity(Property property) {
            return NoneAffinity.class;
        }
    }
}
