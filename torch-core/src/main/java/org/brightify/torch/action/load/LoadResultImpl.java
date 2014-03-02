package org.brightify.torch.action.load;

import android.database.Cursor;
import org.brightify.torch.Torch;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public class LoadResultImpl<ENTITY> implements LoadResult<ENTITY> {
    protected final Torch mTorch;
    protected final LoadQuery<ENTITY> mLoadQuery;

    public LoadResultImpl(Torch torch, LoadQuery<ENTITY> loadQuery) {
        mTorch = torch;
        mLoadQuery = loadQuery;
    }

    @Override
    public void async(Callback<List<ENTITY>> callback) {
        AsyncRunner.run(callback, new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return now();
            }
        });
    }

    @Override
    public Iterator<ENTITY> iterator() {
        Cursor cursor = mLoadQuery.run(mTorch);

        return new CursorIterator<ENTITY>(mLoadQuery.getEntityMetadata(), cursor);
    }

    @Override
    public LinkedList<ENTITY> now() {
        LinkedList<ENTITY> list = new LinkedList<ENTITY>();

        for(ENTITY entity : this) {
            list.addLast(entity);
        }

        return list;
    }

    @Override
    public int count() {
        return mLoadQuery.count(mTorch);
    }
}
