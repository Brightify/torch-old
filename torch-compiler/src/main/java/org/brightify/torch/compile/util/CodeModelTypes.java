package org.brightify.torch.compile.util;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.Property;
import org.brightify.torch.filter.BooleanProperty;
import org.brightify.torch.filter.GenericProperty;
import org.brightify.torch.filter.ListProperty;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationException;

import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class CodeModelTypes {

    // Static codemodel used in whole compilation.
    // FIXME we should not rely on static stateful object!
    public static final JCodeModel CODE_MODEL = new JCodeModel();

    // Java classes
    public static final JClass BYTE = ref(Byte.class.getCanonicalName());
    public static final JClass INTEGER = ref(Integer.class.getCanonicalName());
    public static final JClass LONG = ref(Long.class.getCanonicalName());
    public static final JClass STRING = ref(String.class.getCanonicalName());
    public static final JClass OBJECT = ref(Object.class.getCanonicalName());
    public static final JClass CLASS = ref(Class.class.getCanonicalName());
    public static final JClass LIST = ref(List.class.getCanonicalName());
    // Torch classes
    public static final JClass TORCH = ref(Torch.class.getCanonicalName());
    public static final JClass MIGRATION_ASSISTANT = ref(MigrationAssistant.class.getCanonicalName());
    public static final JClass MIGRATION_EXCEPTION = ref(MigrationException.class.getCanonicalName());
    public static final JClass ENTITY_MIGRATION_TYPE = ref(Entity.MigrationType.class.getCanonicalName());
    public static final JClass KEY = ref(Key.class.getCanonicalName());
    public static final JClass KEY_FACTORY = ref("org.brightify.torch.KeyFactory");
    public static final JClass SERIALIZER = ref("org.brightify.torch.util.Serializer");
    public static final JClass ENTITY_METADATA = ref(EntityMetadata.class.getCanonicalName());
    // Torch properties
    public static final JClass BOOLEAN_PROPERTY = ref(BooleanProperty.class.getCanonicalName());
    public static final JClass BOOLEAN_PROPERTY_IMPL = ref(BOOLEAN_PROPERTY.fullName() + "Impl");
    public static final JClass NUMBER_PROPERTY = ref(NumberProperty.class.getCanonicalName());
    public static final JClass NUMBER_PROPERTY_IMPL = ref(NUMBER_PROPERTY.fullName() + "Impl");
    public static final JClass STRING_PROPERTY = ref(StringProperty.class.getCanonicalName());
    public static final JClass STRING_PROPERTY_IMPL = ref(STRING_PROPERTY.fullName() + "Impl");
    public static final JClass GENERIC_PROPERTY = ref(GenericProperty.class.getCanonicalName());
    public static final JClass GENERIC_PROPERTY_IMPL = ref(GENERIC_PROPERTY.fullName() + "Impl");
    public static final JClass LIST_PROPERTY = ref(ListProperty.class.getCanonicalName());
    public static final JClass LIST_PROPERTY_IMPL = ref(LIST_PROPERTY.fullName() + "Impl");
    // Android classes
    public static final JClass CURSOR = ref("android.database.Cursor");
    public static final JClass CONTENT_VALUES = ref("android.content.ContentValues");

    private CodeModelTypes() {
    }

    public static JClass ref(String fullyQualifiedClassName) {
        return CODE_MODEL.ref(fullyQualifiedClassName);
    }

    public static JClass ref(Class<?> cls) {
        return CODE_MODEL.ref(cls);
    }

    public static JClass ref(Property property) {
        return ref(property.getType().toString());
    }
}
