package com.brightgestures.brightify.util;

import com.brightgestures.brightify.marshall.CursorMarshallerInfo;
import com.brightgestures.brightify.marshall.CursorMarshallerProvider;
import com.brightgestures.brightify.marshall.StreamMarshallerInfo;
import com.brightgestures.brightify.marshall.StreamMarshallerProvider;
import com.brightgestures.brightify.parse.Property;
import org.reflections.Reflections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class TypeHelper {
    private final ProcessingEnvironment environment;
    private final Set<CursorMarshallerProvider> cursorMarshallerProviders = new HashSet<>();
    private final Set<StreamMarshallerProvider> streamMarshallerProviders = new HashSet<>();

    public TypeHelper(ProcessingEnvironment environment) {
        this.environment = environment;

        Reflections reflections = new Reflections();
        Set<Class<? extends CursorMarshallerProvider>> cursorMarshallerProviderClasses = reflections.getSubTypesOf(
                CursorMarshallerProvider.class);

        for (Class<? extends CursorMarshallerProvider> marshallerProviderClass : cursorMarshallerProviderClasses) {
            try {
                CursorMarshallerProvider marshallerProvider = marshallerProviderClass.newInstance();
                cursorMarshallerProviders.add(marshallerProvider);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Couldn't instantiate cursor marshaller provider " + marshallerProviderClass.getSimpleName() +
                        "!", e);
            }
        }

        Set<Class<? extends StreamMarshallerProvider>> streamMarshallerProviderClasses = reflections.getSubTypesOf(
                StreamMarshallerProvider.class);

        for (Class<? extends StreamMarshallerProvider> marshallerProviderClass : streamMarshallerProviderClasses) {
            try {
                StreamMarshallerProvider marshallerProvider = marshallerProviderClass.newInstance();
                streamMarshallerProviders.add(marshallerProvider);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Couldn't instantiate stream marshaller provider " + marshallerProviderClass.getSimpleName() +
                        "!", e);
            }
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

    public CursorMarshallerInfo getCursorMarshallerInfo(Property property) {
        for (CursorMarshallerProvider cursorMarshallerProvider : cursorMarshallerProviders) {
            if (!cursorMarshallerProvider.isSupported(this, property)) {
                continue;
            }
            CursorMarshallerInfo marshallerInfo = cursorMarshallerProvider.getMarshallerInfo(this, property);
            if (marshallerInfo == null) {
                throw new IllegalStateException(
                        "Marshaller provider " + cursorMarshallerProvider.getClass().getSimpleName() +
                        " returned null as marshaller info even though it reports type " +
                        property.type + " as supported!");
            }
            return marshallerInfo;
        }
        return null;
    }

    public StreamMarshallerInfo getStreamMarshallerInfo(TypeMirror typeMirror) {
        for (StreamMarshallerProvider streamMarshallerProvider : streamMarshallerProviders) {
            if (!streamMarshallerProvider.isSupported(this, typeMirror)) {
                continue;
            }
            StreamMarshallerInfo marshallerInfo = streamMarshallerProvider.getMarshallerInfo(this, typeMirror);
            if (marshallerInfo == null) {
                throw new IllegalStateException(
                        "Marshaller provider " + streamMarshallerProvider.getClass().getSimpleName() +
                        " returned null as marshaller info even though it reports type " +
                        typeMirror + " as supported!");
            }
            return marshallerInfo;
        }
        return null;
    }

    public TypeElement elementOf(Class cls) {
        return environment.getElementUtils().getTypeElement(cls.getName());
    }

    public TypeMirror typeOf(Class cls) {
        return elementOf(cls).asType();
    }

    private DeclaredType listOf(Class<?> bound) {
        return listOf(bound != null ? typeOf(bound) : null);
    }

    private DeclaredType listOf(TypeMirror bound) {
        return environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(
                "java.util.List"), bound);
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

    private static void addSupportedClass(Class<?> cls, Class<? extends AbstractTypeAffinity> affinity,
    String fromCursor) {
        addSupportedClass(cls, affinity, fromCursor, "%getter%");
    }

    private static void addSupportedClass(Class<?> cls, Class<? extends AbstractTypeAffinity> affinity,
    String fromCursor,
                                          String toCursor) {
        addSupportedType(typeOf(cls), affinity, fromCursor, toCursor);
    }

    private static void addSupportedType(TypeMirror typeMirror, String fromCursor, String toCursor) {
        addSupportedType(typeMirror, NoneAffinity.class, fromCursor, toCursor);
    }

    private static void addSupportedType(TypeMirror typeMirror, Class<? extends AbstractTypeAffinity> affinity,
    String fromCursor,
                                         String toCursor) {
        assert !SUPPORTED_TYPES_.contains(typeMirror);

        SUPPORTED_TYPES_.add(typeMirror);
        TYPE_TO_AFFINITY.put(typeMirror, affinity);
        FROM_CURSOR.put(typeMirror, fromCursor);
        TO_CURSOR.put(typeMirror, toCursor);
    }
*/


}
