package org.brightify.torch.compile.util;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.Property;
import org.brightify.torch.filter.BooleanColumn;
import org.brightify.torch.filter.GenericColumn;
import org.brightify.torch.filter.NumberColumn;
import org.brightify.torch.filter.StringColumn;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationException;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class CodeModelTypes {
    private static final JCodeModel codeModel = new JCodeModel();

    // Java classes
    public static final String S_BYTE = Byte.class.getCanonicalName();
    public static final String S_INTEGER = Integer.class.getCanonicalName();
    public static final String S_LONG = Long.class.getCanonicalName();
    public static final String S_STRING = String.class.getCanonicalName();
    public static final String S_OBJECT = Object.class.getCanonicalName();
    public static final String S_CLASS = Class.class.getCanonicalName();

    // Torch CORE classes
    public static final String S_TORCH = Torch.class.getCanonicalName();
    public static final String S_MIGRATION_ASSISTANT = MigrationAssistant.class.getCanonicalName();
    public static final String S_MIGRATION_EXCEPTION = MigrationException.class.getCanonicalName();
    public static final String S_ENTITY_MIGRATION_TYPE = Entity.MigrationType.class.getCanonicalName();
    public static final String S_KEY = Key.class.getCanonicalName();
    public static final String S_KEY_FACTORY = "org.brightify.torch.KeyFactory";
    public static final String S_SERIALIZER = "org.brightify.torch.util.Serializer";

    public static final String S_BOOLEAN_COLUMN = BooleanColumn.class.getCanonicalName();
    public static final String S_BOOLEAN_COLUMN_IMPL = S_BOOLEAN_COLUMN + "Impl"; // FIXME is + "Impl" enough?
    public static final String S_NUMBER_COLUMN = NumberColumn.class.getCanonicalName();
    public static final String S_NUMBER_COLUMN_IMPL = S_NUMBER_COLUMN + "Impl";
    public static final String S_STRING_COLUMN = StringColumn.class.getCanonicalName();
    public static final String S_STRING_COLUMN_IMPL = S_STRING_COLUMN + "Impl";
    public static final String S_GENERIC_COLUMN = GenericColumn.class.getCanonicalName();
    public static final String S_GENERIC_COLUMN_IMPL = S_GENERIC_COLUMN + "Impl";

    // Android classes
    public static final String S_CURSOR = "android.database.Cursor";
    public static final String S_CONTENT_VALUES = "android.content.ContentValues";

    public static final JClass BYTE = ref(S_BYTE);
    public static final JClass INTEGER = ref(S_INTEGER);
    public static final JClass LONG = ref(S_LONG);
    public static final JClass STRING = ref(S_STRING);
    public static final JClass OBJECT = ref(S_OBJECT);
    public static final JClass CLASS = ref(S_CLASS);

    public static final JClass TORCH = ref(S_TORCH);
    public static final JClass MIGRATION_ASSISTANT = ref(S_MIGRATION_ASSISTANT);
    public static final JClass MIGRATION_EXCEPTION = ref(S_MIGRATION_EXCEPTION);
    public static final JClass ENTITY_MIGRATION_TYPE = ref(S_ENTITY_MIGRATION_TYPE);
    public static final JClass KEY = ref(S_KEY);
    public static final JClass KEY_FACTORY = ref(S_KEY_FACTORY);
    public static final JClass SERIALIZER = ref(S_SERIALIZER);

    public static final JClass BOOLEAN_COLUMN = ref(S_BOOLEAN_COLUMN);
    public static final JClass BOOLEAN_COLUMN_IMPL = ref(S_BOOLEAN_COLUMN_IMPL);
    public static final JClass NUMBER_COLUMN = ref(S_NUMBER_COLUMN);
    public static final JClass NUMBER_COLUMN_IMPL = ref(S_NUMBER_COLUMN_IMPL);
    public static final JClass STRING_COLUMN = ref(S_STRING_COLUMN);
    public static final JClass STRING_COLUMN_IMPL = ref(S_STRING_COLUMN_IMPL);
    public static final JClass GENERIC_COLUMN = ref(S_GENERIC_COLUMN);
    public static final JClass GENERIC_COLUMN_IMPL = ref(S_GENERIC_COLUMN_IMPL);

    public static final JClass CURSOR = ref(S_CURSOR);
    public static final JClass CONTENT_VALUES =ref(S_CONTENT_VALUES);

    private CodeModelTypes() {

    }

    public static JClass ref(String fullyQualifiedClassName) {
        return codeModel.ref(fullyQualifiedClassName);
    }

    public static JClass ref(Class<?> cls) {
        return codeModel.ref(cls);
    }

    public static JClass ref(Property property) {
        return ref(property.getType().toString());
    }

    public static JCodeModel getCodeModel() {
        return codeModel;
    }
}
