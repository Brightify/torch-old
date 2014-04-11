package org.brightify.torch.util;

import com.google.inject.Inject;
import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.filter.ColumnProvider;
import org.brightify.torch.filter.DefaultColumnProvider;
import org.brightify.torch.marshall.CursorMarshallerInfo;
import org.brightify.torch.marshall.CursorMarshallerProvider;
import org.brightify.torch.marshall.StreamMarshallerInfo;
import org.brightify.torch.marshall.StreamMarshallerProvider;
import org.brightify.torch.parse.Property;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TypeHelperImpl implements TypeHelper {
    private final ProcessingEnvironment environment;
    private final Set<CursorMarshallerProvider> cursorMarshallerProviders = new HashSet<CursorMarshallerProvider>();
    private final Set<StreamMarshallerProvider> streamMarshallerProviders = new HashSet<StreamMarshallerProvider>();
    private final Set<ColumnProvider> columnProviders = new HashSet<ColumnProvider>();
    private final ColumnProvider defaultColumnProvider = new DefaultColumnProvider();

    @Inject
    public TypeHelperImpl(ProcessingEnvironment environment, Reflections reflections) {
        this.environment = environment;

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

        Set<Class<? extends ColumnProvider>> columnProviderClasses = reflections.getSubTypesOf(ColumnProvider.class);

        for (Class<? extends ColumnProvider> columnProviderClass : columnProviderClasses) {
            // DefaultColumnProvider handles all columns that aren't supported so it can't be added to providers.
            if(columnProviderClass == DefaultColumnProvider.class) {
                continue;
            }
            try {
                ColumnProvider columnProvider = columnProviderClass.newInstance();
                columnProviders.add(columnProvider);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't instantiate column provider " + columnProviderClass
                        .getSimpleName() + "!", e);
            }
        }
    }

    @Override
    public ProcessingEnvironment getProcessingEnvironment() {
        return environment;
    }

    @Override
    public TypeMirror getWrappedType(Property property) {
        return getWrappedType(property.getType());
    }

    @Override
    public TypeMirror getWrappedType(TypeMirror propertyType) {
        if (propertyType.getKind().isPrimitive()) {
            propertyType = environment.getTypeUtils().boxedClass(
                    (PrimitiveType) propertyType).asType();
        }
        return propertyType;
    }

    @Override
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
                                property.getType() + " as supported!");
            }
            return marshallerInfo;
        }
        return null;
    }

    @Override
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

    @Override
    public ColumnInfo getColumnInfo(Property property) {
        for (ColumnProvider columnProvider : columnProviders) {
            if (!columnProvider.isSupported(this, property)) {
                continue;
            }
            ColumnInfo columnInfo = columnProvider.getColumnInfo(this, property);
            if (columnInfo == null) {
                throw new IllegalStateException("Column provider " + columnProvider.getClass().getSimpleName() + " " +
                        "returned null as column info even through it reports type" + property.getType() + " as supported!");
            }
            return columnInfo;
        }
        return defaultColumnProvider.getColumnInfo(this, property);
    }

    @Override
    public TypeElement elementOf(Class cls) {
        return environment.getElementUtils().getTypeElement(cls.getName());
    }

    @Override
    public TypeMirror typeOf(Class cls) {
        return elementOf(cls).asType();
    }

    @Override
    public Class<?> classOf(TypeMirror mirror) {
        try {
            return Class.forName(mirror.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DeclaredType listOf(Class<?> bound) {
        return listOf(bound != null ? typeOf(bound) : null);
    }

    private DeclaredType listOf(TypeMirror bound) {
        return environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(
                "java.util.List"), bound);
    }

    @Override
    public String packageOf(Element element) {
        String[] entityPackages = element.toString().split("\\.");
        if (entityPackages.length == 1) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < entityPackages.length - 1; i++) {
                if (i > 0) {
                    builder.append(".");
                }
                builder.append(entityPackages[i]);
            }
            return builder.toString();
        }
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
