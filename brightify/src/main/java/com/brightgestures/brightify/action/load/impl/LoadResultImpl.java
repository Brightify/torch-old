package com.brightgestures.brightify.action.load.impl;

import android.database.Cursor;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.action.load.CursorIterator;
import com.brightgestures.brightify.action.load.LoadQuery;
import com.brightgestures.brightify.action.load.api.LoadResult;
import com.brightgestures.brightify.util.Callback;

import java.util.ArrayList;
import java.util.Iterator;
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
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Iterator<ENTITY> iterator() {
        Cursor cursor = mLoadQuery.run();

        return new CursorIterator<ENTITY>(mLoadQuery.getEntityMetadata(), cursor);
    }

    @Override
    public ArrayList<ENTITY> now() {
        ArrayList<ENTITY> list = new ArrayList<ENTITY>();

        for(ENTITY entity : this) {
            list.add(entity);
        }

        return list;
    }
}
