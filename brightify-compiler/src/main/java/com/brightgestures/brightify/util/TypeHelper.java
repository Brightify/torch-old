package com.brightgestures.brightify.util;

import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.sql.TypeName;
import com.brightgestures.brightify.type.CollectionTypeSet;
import com.brightgestures.brightify.type.GenericTypeSet;
import com.brightgestures.brightify.type.InternalTypeSet;
import com.brightgestures.brightify.SupportedType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class TypeHelper {
    private final ProcessingEnvironment environment;
    private final List<SupportedType> supportedTypes = new LinkedList<>();

    public TypeHelper(ProcessingEnvironment environment) {
        this.environment = environment;

        addSupportedTypes(GenericTypeSet.getAll(this));
        addSupportedTypes(InternalTypeSet.getAll(this));
        addSupportedTypes(CollectionTypeSet.getAll(this));
    }

    private void addSupportedTypes(SupportedType[] supportedTypes) {
        Collections.addAll(this.supportedTypes, supportedTypes);
    }

    private void addSupportedTypes(Iterable<? extends SupportedType> supportedTypes) {
        for(SupportedType supportedType : supportedTypes) {
            this.supportedTypes.add(supportedType);
        }
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return environment;
    }

    public TypeMirror getWrappedType(Property property) {
        return getWrappedType(property.type);
    }

    public TypeMirror getWrappedType(TypeMirror propertyType) {
        if (propertyType.getKind().isPrimitive()) {
            propertyType = environment.getTypeUtils().boxedClass(
                    (PrimitiveType) propertyType).asType();
        }
        return propertyType;
    }

    public Class<? extends TypeName> affinityClassFromProperty(Property property) {
        SupportedType supportedTypeSet = supportedTypeSet(property);

        if (supportedTypeSet != null) {
            return supportedTypeSet.getAffinity(property);
        } else {
            return null;
        }
    }

    public String affinityFromProperty(Property property) {
        return "new " + affinityClassFromProperty(property).getName() + "()";
    }

    public TypeMirror typeOf(Class cls) {
        return environment.getElementUtils().getTypeElement(cls.getName()).asType();
    }

    private DeclaredType listOf(Class<?> bound) {
        return listOf(bound != null ? typeOf(bound) : null);
    }

    private DeclaredType listOf(TypeMirror bound) {
        return environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(
                "java.util.List"), bound);
    }

    public SupportedType supportedTypeSet(Property property) {
        for (SupportedType supportedType : supportedTypes) {
            if (supportedType.isSupported(property)) {
                return supportedType;
            }
        }

        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type " + property.type +
                                                                      " is not supported (see @Marshall annotation)");
        return null;
    }


/*
    public static void prepare(ProcessingEnvironment environment) {
        sEnvironment = environment;

        addSupportedClass(Boolean.class, IntegerAffinity.class, "cursor.getInt(index) > 0");
        addSupportedClass(Byte.class, IntegerAffinity.class, "(Byte) cursor.getInt(index)");
        addSupportedClass(Short.class, IntegerAffinity.class, "cursor.getShort(index)");
        addSupportedClass(Integer.class, IntegerAffinity.class, "cursor.getInt(index)");
        addSupportedClass(Long.class, IntegerAffinity.class, "cursor.getLong(index)");

        addSupportedClass(Float.class, RealAffinity.class, "cursor.getFloat(index)");
        addSupportedClass(Double.class, RealAffinity.class, "cursor.getDouble(index)");

        addSupportedClass(String.class, TextAffinity.class, "cursor.getString(index)");

//        addSupportedClass(byte[].class, "cursor.getBlob(index)");
        addSupportedClass(Key.class, "Key.keyFromByteArray(cursor.getBlob(index)");
        addSupportedType(listOf(String.class), "", "");

    }

    private static void addSupportedClass(Class<?> cls, String fromCursor) {
        addSupportedClass(cls, NoneAffinity.class, fromCursor);
    }

    private static void addSupportedClass(Class<?> cls, Class<? extends TypeName> affinity, String fromCursor) {
        addSupportedClass(cls, affinity, fromCursor, "%getter%");
    }

    private static void addSupportedClass(Class<?> cls, Class<? extends TypeName> affinity, String fromCursor,
                                          String toCursor) {
        addSupportedType(typeOf(cls), affinity, fromCursor, toCursor);
    }

    private static void addSupportedType(TypeMirror typeMirror, String fromCursor, String toCursor) {
        addSupportedType(typeMirror, NoneAffinity.class, fromCursor, toCursor);
    }

    private static void addSupportedType(TypeMirror typeMirror, Class<? extends TypeName> affinity, String fromCursor,
                                         String toCursor) {
        assert !SUPPORTED_TYPES_.contains(typeMirror);

        SUPPORTED_TYPES_.add(typeMirror);
        TYPE_TO_AFFINITY.put(typeMirror, affinity);
        FROM_CURSOR.put(typeMirror, fromCursor);
        TO_CURSOR.put(typeMirror, toCursor);
    }
*/


}
