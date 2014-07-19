package org.brightify.torch.marshall;

import org.brightify.torch.RawEntity;
import org.brightify.torch.sql.TypeAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface CursorMarshaller<INPUT_TYPE, OUTPUT_TYPE extends INPUT_TYPE> {

    void marshall(RawEntity contentValues, String columnName, INPUT_TYPE value) throws Exception;

    OUTPUT_TYPE unmarshall(RawEntity cursor, String columnName) throws Exception;

    TypeAffinity getAffinity();

}
