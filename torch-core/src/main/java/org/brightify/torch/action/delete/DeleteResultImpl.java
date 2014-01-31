package org.brightify.torch.action.delete;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.Torch;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.action.delete.DeleteResult;
import org.brightify.torch.util.Callback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class DeleteResultImpl<ENTITY> implements DeleteResult<ENTITY> {

    protected final Torch mTorch;
    protected final Collection<Key<ENTITY>> mEntityKeys;

    public DeleteResultImpl(Torch torch, Collection<Key<ENTITY>> entityKeys) {
        mTorch = torch;
        mEntityKeys = entityKeys;
    }

    @Override
    public Map<Key<ENTITY>, Boolean> now() {
        SQLiteDatabase db = mTorch.getDatabase();

        db.beginTransaction();
        try {
            Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();
            EntityMetadata<ENTITY> metadata = mTorch.getFactory().getEntities().getMetadata(
                    mEntityKeys.iterator().next().getType());
            for (Key<ENTITY> key : mEntityKeys) {
                int affected = db.delete(metadata.getTableName(), metadata.getIdColumnName() + " = ?",
                        new String[] { String.valueOf(key.getId()) });
                if (affected > 1) {
                    throw new IllegalStateException("Delete command affected more than one row at once!");
                }

                results.put(key, affected == 1);
            }

            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void async(Callback<Map<Key<ENTITY>, Boolean>> callback) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
