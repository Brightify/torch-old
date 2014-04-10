package org.brightify.torch.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import org.brightify.torch.marshall.stream.ArrayListStreamMarshaller;
import org.brightify.torch.marshall.CursorMarshaller;
import org.brightify.torch.marshall.StreamMarshaller;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.NoneAffinity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ArrayListCursorMarshaller<ITEM_TYPE> implements CursorMarshaller<List<ITEM_TYPE>, ArrayList<ITEM_TYPE>> {

    private static final Map<StreamMarshaller<?, ?>, ArrayListCursorMarshaller<?>> instances =
            new HashMap<StreamMarshaller<?, ?>, ArrayListCursorMarshaller<?>>();
    private final StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller;

    private ArrayListCursorMarshaller(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        this.itemMarshaller = itemMarshaller;
    }

    @Override
    public void marshall(ContentValues contentValues, String columnName, List<ITEM_TYPE> value) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        ArrayListStreamMarshaller.getInstance(itemMarshaller).marshall(dataOutputStream, value);

        dataOutputStream.flush();
        contentValues.put(columnName, byteArrayOutputStream.toByteArray());
    }

    @Override
    public ArrayList<ITEM_TYPE> unmarshall(Cursor cursor, String columnName) throws Exception {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cursor.getBlob(index));
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        return ArrayListStreamMarshaller.getInstance(itemMarshaller).unmarshall(dataInputStream);
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    public static <ITEM_TYPE> ArrayListCursorMarshaller<ITEM_TYPE> getInstance(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        if (!instances.containsKey(itemMarshaller)) {
            instances.put(itemMarshaller, new ArrayListCursorMarshaller<ITEM_TYPE>(itemMarshaller));
        }
        return (ArrayListCursorMarshaller<ITEM_TYPE>) instances.get(itemMarshaller);
    }
}
