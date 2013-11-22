package com.brightgestures.brightify.type;

import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.SupportedType;
import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.sql.AbstractTypeAffinity;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;
import com.brightgestures.brightify.sql.affinity.RealAffinity;
import com.brightgestures.brightify.sql.affinity.TextAffinity;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class GenericTypeSet {

    public static SupportedType[] getAll(TypeHelper typeHelper) {

        return new SupportedType[] {
                GenericType.create(typeHelper, Boolean.class, "cursor.getShort(index) > 0", IntegerAffinity.class),
                GenericType.create(typeHelper, Byte.class, "(Byte) cursor.getInt(index)", IntegerAffinity.class),
                GenericType.create(typeHelper, Short.class, "cursor.getShort(index)", IntegerAffinity.class),
                GenericType.create(typeHelper, Integer.class, "cursor.getInt(index)", IntegerAffinity.class),
                GenericType.create(typeHelper, Long.class, "cursor.getLong(index)", IntegerAffinity.class),
                GenericType.create(typeHelper, Float.class, "cursor.getFloat(index)", RealAffinity.class),
                GenericType.create(typeHelper, Double.class, "cursor.getDouble(index)", RealAffinity.class),
                GenericType.create(typeHelper, String.class, "cursor.getString(index)", TextAffinity.class)
        };
    }

    public static class GenericType implements SupportedType {
        public final TypeHelper typeHelper;
        public final TypeMirror type;
        public final String readPhrase;
        public final Class<? extends AbstractTypeAffinity> affinity;

        private GenericType(TypeHelper typeHelper, TypeMirror type, String readPhrase,
                            Class<? extends AbstractTypeAffinity> affinity) {
            this.typeHelper = typeHelper;
            this.type = type;
            this.readPhrase = readPhrase;
            this.affinity = affinity;
        }

        @Override
        public boolean isSupported(Property property) {
            TypeMirror propertyType = typeHelper.getWrappedType(property);
            return typeHelper.getProcessingEnvironment().getTypeUtils().isSameType(propertyType, type);
        }

        @Override
        public void read(Property property, SourceFileGenerator sourceFileGenerator) {
            sourceFileGenerator.line("entity.")
                               .append(property.setValue(readPhrase))
                               .append(";")
                               .append(" // ")
                               .append(property.type);
        }

        @Override
        public void write(Property property, SourceFileGenerator sourceFileGenerator) {
            sourceFileGenerator.line("values.put(\"")
                               .append(property.columnName)
                               .append("\", entity.")
                               .append(property.getValue())
                               .append("); // ")
                               .append(property.type);
        }

        @Override
        public Class<? extends AbstractTypeAffinity> getAffinity(Property property) {
            return affinity;
        }

        public static GenericType create(TypeHelper typeHelper, Class<?> cls, String readPhrase,
                                         Class<? extends AbstractTypeAffinity> affinity) {
            TypeMirror type = typeHelper.typeOf(cls);
            return new GenericType(typeHelper, type, readPhrase, affinity);
        }
    }
}
