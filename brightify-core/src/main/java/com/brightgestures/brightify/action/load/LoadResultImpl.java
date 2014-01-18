package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.util.AsyncRunner;
import com.brightgestures.brightify.util.Callback;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public class LoadResultImpl<ENTITY> implements LoadResult<ENTITY> {
    protected final Brightify mBrightify;
    protected final LoadQuery<ENTITY> mLoadQuery;

    public LoadResultImpl(Brightify brightify, LoadQuery<ENTITY> loadQuery) {
        mBrightify = brightify;
        mLoadQuery = loadQuery;
    }

    @Override
    public void async(Callback<List<ENTITY>> callback) {
        AsyncRunner.run(new AsyncRunner.Task<List<ENTITY>>() {
            @Override
            public List<ENTITY> doWork() throws Exception {
                return now();
            }
        }, callback);
    }

    @Override
    public Iterator<ENTITY> iterator() {
        Cursor cursor = mLoadQuery.run(mBrightify);

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
}
