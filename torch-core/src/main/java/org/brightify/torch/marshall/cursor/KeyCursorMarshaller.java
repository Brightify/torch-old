package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.Key;
import org.brightify.torch.marshall.SymetricCursorMarshaller;
import org.brightify.torch.marshall.stream.KeyStreamMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.NoneAffinity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class KeyCursorMarshaller<ENTITY> implements SymetricCursorMarshaller<Key<ENTITY>> {

    private static final Map<Class<?>, KeyCursorMarshaller<?>> instances =
            new HashMap<Class<?>, KeyCursorMarshaller<?>>();
    private final Class<ENTITY> entityClass;

    private KeyCursorMarshaller(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void marshall(ContentValues contentValues, String columnName, Key<ENTITY> value) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        KeyStreamMarshaller.getInstance(entityClass).marshall(dataOutputStream, value);

        dataOutputStream.flush();
        contentValues.put(columnName, byteArrayOutputStream.toByteArray());
    }

    @Override
    public Key<ENTITY> unmarshall(Cursor cursor, String columnName) throws Exception {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cursor.getBlob(index));
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        return KeyStreamMarshaller.getInstance(entityClass).unmarshall(dataInputStream);
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    public static <ENTITY> KeyCursorMarshaller<ENTITY> getInstance(Class<ENTITY> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("Keys has to have entityClass!");
        }
        if (!instances.containsKey(entityClass)) {
            instances.put(entityClass, new KeyCursorMarshaller<ENTITY>(entityClass));
        }
        return (KeyCursorMarshaller<ENTITY>) instances.get(entityClass);
    }
}
