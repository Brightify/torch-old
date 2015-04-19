package org.brightify.torch.compile.util;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.LoadContainer;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.Ref;
import org.brightify.torch.RefCollection;
import org.brightify.torch.SaveContainer;
import org.brightify.torch.Torch;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.SourceCodeWriter;
import org.brightify.torch.filter.BooleanProperty;
import org.brightify.torch.filter.GenericProperty;
import org.brightify.torch.filter.ListProperty;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ReferenceCollectionProperty;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.impl.filter.BooleanPropertyImpl;
import org.brightify.torch.impl.filter.ByteArrayPropertyImpl;
import org.brightify.torch.impl.filter.BytePropertyImpl;
import org.brightify.torch.impl.filter.DoublePropertyImpl;
import org.brightify.torch.impl.filter.FloatPropertyImpl;
import org.brightify.torch.impl.filter.GenericPropertyImpl;
import org.brightify.torch.impl.filter.IntegerPropertyImpl;
import org.brightify.torch.impl.filter.ListPropertyImpl;
import org.brightify.torch.impl.filter.LongPropertyImpl;
import org.brightify.torch.impl.filter.NumberPropertyImpl;
import org.brightify.torch.impl.filter.PropertyImpl;
import org.brightify.torch.impl.filter.ReferenceCollectionPropertyImpl;
import org.brightify.torch.impl.filter.ReferencePropertyImpl;
import org.brightify.torch.impl.filter.SerializablePropertyImpl;
import org.brightify.torch.impl.filter.ShortPropertyImpl;
import org.brightify.torch.impl.filter.StringPropertyImpl;
import org.brightify.torch.util.ArrayListBuilder;
import org.brightify.torch.util.LazyArrayList;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class CodeModelTypes {

    // Static codemodel used in whole compilation.
    // FIXME we should not rely on static stateful object!
    public static JCodeModel CODE_MODEL = new JCodeModel();

    // Java primitives
    public static final JClass BYTE_PRIMITIVE = ref(byte.class.getCanonicalName());
    public static final JClass INTEGER_PRIMITIVE = ref(int.class.getCanonicalName());
    public static final JClass LONG_PRIMITIVE = ref(long.class.getCanonicalName());

    // Java classes
    public static final JClass BYTE = ref(Byte.class.getCanonicalName());
    public static final JClass INTEGER = ref(Integer.class.getCanonicalName());
    public static final JClass LONG = ref(Long.class.getCanonicalName());
    public static final JClass STRING = ref(String.class.getCanonicalName());
    public static final JClass OBJECT = ref(Object.class.getCanonicalName());
    public static final JClass CLASS = ref(Class.class.getCanonicalName());
    public static final JClass LIST = ref(List.class.getCanonicalName());
    public static final JClass ARRAY_LIST = ref(ArrayList.class.getCanonicalName());
    public static final JClass SET = ref(Set.class.getCanonicalName());
    public static final JClass HASH_SET = ref(HashSet.class.getCanonicalName());
    public static final JClass WILDCARD = OBJECT.wildcard();
    public static final JClass ANNOTATION = ref(Annotation.class.getCanonicalName());
    public static final JClass COLLECTION = ref(Collection.class.getCanonicalName());

    // Torch classes
    public static final JClass TORCH = ref(Torch.class.getCanonicalName());
    public static final JClass TORCH_FACTORY = ref(TorchFactory.class.getCanonicalName());
    public static final JClass LOAD_CONTAINER = ref(LoadContainer.class.getCanonicalName());
    public static final JClass SAVE_CONTAINER = ref(SaveContainer.class.getCanonicalName());
    public static final JClass MIGRATION_ASSISTANT = ref(MigrationAssistant.class.getCanonicalName());
    public static final JClass MIGRATION_EXCEPTION = ref(MigrationException.class.getCanonicalName());
    public static final JClass ENTITY_MIGRATION_TYPE = ref(Entity.MigrationType.class.getCanonicalName());
    public static final JClass KEY = ref(Key.class.getCanonicalName());
    public static final JClass KEY_FACTORY = ref("org.brightify.torch.KeyFactory");
    public static final JClass SERIALIZER = ref("org.brightify.torch.util.Serializer");
    public static final JClass ENTITY_METADATA = ref(EntityDescription.class.getCanonicalName());
    public static final JClass LAZY_ARRAY_LIST = ref(LazyArrayList.class.getCanonicalName());
    public static final JClass READABLE_RAW_ENTITY = ref(ReadableRawEntity.class.getCanonicalName());
    public static final JClass WRITABLE_RAW_ENTITY = ref(WritableRawEntity.class.getCanonicalName());
    public static final JClass REF = ref(Ref.class.getCanonicalName());
    public static final JClass REF_COLLECTION = ref(RefCollection.class.getCanonicalName());
    public static final JClass ARRAY_LIST_BUILDER = ref(ArrayListBuilder.class.getCanonicalName());
    public static final JClass FEATURE = ref(Property.Feature.class);

    // Torch getProperties
    public static final JClass PROPERTY = ref(Property.class.getCanonicalName());
    public static final JClass PROPERTY_IMPL = ref(PropertyImpl.class.getCanonicalName());
    public static final JClass BOOLEAN_PROPERTY = ref(BooleanProperty.class.getCanonicalName());
    public static final JClass BOOLEAN_PROPERTY_IMPL = ref(BooleanPropertyImpl.class.getCanonicalName());
    public static final JClass NUMBER_PROPERTY = ref(NumberProperty.class.getCanonicalName());
    public static final JClass NUMBER_PROPERTY_IMPL = ref(NumberPropertyImpl.class.getCanonicalName());
    public static final JClass BYTE_PROPERTY_IMPL = ref(BytePropertyImpl.class.getCanonicalName());
    public static final JClass SHORT_PROPERTY_IMPL = ref(ShortPropertyImpl.class.getCanonicalName());
    public static final JClass INTEGER_PROPERTY_IMPL = ref(IntegerPropertyImpl.class.getCanonicalName());
    public static final JClass LONG_PROPERTY_IMPL = ref(LongPropertyImpl.class.getCanonicalName());
    public static final JClass DOUBLE_PROPERTY_IMPL = ref(DoublePropertyImpl.class.getCanonicalName());
    public static final JClass FLOAT_PROPERTY_IMPL = ref(FloatPropertyImpl.class.getCanonicalName());
    public static final JClass BYTE_ARRAY_PROPERTY_IMPL = ref(ByteArrayPropertyImpl.class.getCanonicalName());
    public static final JClass SERIALIZABLE_PROPERTY_IMPL = ref(SerializablePropertyImpl.class.getCanonicalName());
    public static final JClass STRING_PROPERTY = ref(StringProperty.class.getCanonicalName());
    public static final JClass STRING_PROPERTY_IMPL = ref(StringPropertyImpl.class.getCanonicalName());
    public static final JClass GENERIC_PROPERTY = ref(GenericProperty.class.getCanonicalName());
    public static final JClass GENERIC_PROPERTY_IMPL = ref(GenericPropertyImpl.class.getCanonicalName());
    public static final JClass LIST_PROPERTY = ref(ListProperty.class.getCanonicalName());
    public static final JClass LIST_PROPERTY_IMPL = ref(ListPropertyImpl.class.getCanonicalName());
    public static final JClass VALUE_PROPERTY = ref(ValueProperty.class.getCanonicalName());
    public static final JClass REFERENCE_PROPERTY = ref(ReferenceProperty.class.getCanonicalName());
    public static final JClass REFERENCE_PROPERTY_IMPL = ref(ReferencePropertyImpl.class.getCanonicalName());
    public static final JClass REFERENCE_COLLECTION_PROPERTY = ref(ReferenceCollectionProperty.class.getCanonicalName());
    public static final JClass REFERENCE_COLLECTION_PROPERTY_IMPL = ref(ReferenceCollectionPropertyImpl.class.getCanonicalName());

    private CodeModelTypes() { }

    public static JClass ref(String fullyQualifiedClassName) {
        return CODE_MODEL.ref(fullyQualifiedClassName);
    }

    public static JClass ref(Class<?> cls) {
        return CODE_MODEL.ref(cls);
    }

    public static JClass ref(PropertyMirror propertyMirror) {
        return ref(propertyMirror.getType().toString());
    }

    public static JClass ref(EntityMirror entityMirror) {
        return ref(entityMirror.getFullName());
    }

    public static void build(SourceCodeWriter sourceCodeWriter) throws IOException {
        CODE_MODEL.build(sourceCodeWriter);

        // Resetting JCodeModel after the build to ensure its freshness
        CODE_MODEL = new JCodeModel();
    }
}
