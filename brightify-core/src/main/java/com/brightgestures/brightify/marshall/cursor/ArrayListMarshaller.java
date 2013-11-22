package com.brightgestures.brightify.marshall.cursor;

import android.content.ContentValues;
import android.database.Cursor;
import com.brightgestures.brightify.marshall.CursorMarshaller;
import com.brightgestures.brightify.marshall.StreamMarshaller;
import com.brightgestures.brightify.sql.TypeAffinity;
import com.brightgestures.brightify.sql.affinity.NoneAffinity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ArrayListMarshaller<ITEM_TYPE> implements CursorMarshaller<List<ITEM_TYPE>, ArrayList<ITEM_TYPE>> {

    private static final Map<StreamMarshaller<?, ?>, ArrayListMarshaller<?>> instances =
            new HashMap<StreamMarshaller<?, ?>, ArrayListMarshaller<?>>();
    private final StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller;

    private ArrayListMarshaller(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        this.itemMarshaller = itemMarshaller;
    }

    @Override
    public void marshall(ContentValues contentValues, String columnName, List<ITEM_TYPE> value) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(value.size());
        for (ITEM_TYPE item : value) {
            itemMarshaller.marshall(dataOutputStream, item);
        }
        dataOutputStream.flush();
        contentValues.put(columnName, byteArrayOutputStream.toByteArray());
    }

    @Override
    public ArrayList<ITEM_TYPE> unmarshall(Cursor cursor, String columnName) throws Exception {
        int index = cursor.getColumnIndexOrThrow(columnName);
        if (cursor.isNull(index)) {
            return null;
        }
        ArrayList<ITEM_TYPE> items = new ArrayList<ITEM_TYPE>();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cursor.getBlob(index));
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        int count = dataInputStream.readInt();
        for (int i = 0; i < count; i++) {
            ITEM_TYPE item = itemMarshaller.unmarshall(dataInputStream);
            items.add(item);
        }
        return items;
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    public static <ITEM_TYPE> ArrayListMarshaller<ITEM_TYPE> getInstance(StreamMarshaller<ITEM_TYPE, ITEM_TYPE> itemMarshaller) {
        if (!instances.containsKey(itemMarshaller)) {
            instances.put(itemMarshaller, new ArrayListMarshaller<ITEM_TYPE>(itemMarshaller));
        }
        return (ArrayListMarshaller<ITEM_TYPE>) instances.get(itemMarshaller);
    }
}
