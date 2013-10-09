package com.brightgestures.brightify;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Ref<T> {

    protected final Brightify mBrightify;

    protected Key<T> mKey;
    protected T mValue;
    protected boolean mLoaded = false;

    public Ref(Context context, Key<T> key) {
        this(BrightifyService.bfy(context), key);
    }

    public Ref(Brightify brightify, Key<T> key) {
        mBrightify = brightify;
        mKey = key;
    }

    public T get() {
        if(mValue == null && !mLoaded) {
            mValue = mBrightify.load().key(mKey).now();
            mLoaded = true;
        }
        return mValue;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public static <T> String refToString(Ref<T> ref) {
        if(ref != null) {
            return refsToString(ref);
        } else {
            return refsToString();
        }
    }

    public static <T> String refToString(Brightify brightify, boolean saveValue, Ref<T> ref) {
        if(ref != null) {
            return refsToString(brightify, saveValue, ref);
        } else {
            return refsToString(brightify, saveValue);
        }
    }

    public static <T> String refsToString(Ref<T>... refs) {
        return refsToString(null, false, refs);
    }

    public static <T> String refsToString(Brightify brightify, boolean saveValues, Ref<T>... refs) {
        if(saveValues && brightify == null) {
            throw new IllegalArgumentException("Cannot save values without Brightify instance!");
        }
        if(refs.length == 0) {
            return null;
        }

        if(saveValues) {
            LinkedList<T> entities = new LinkedList<T>();

            for(Ref<T> ref : refs) {
                entities.addLast(ref.mValue);
            }

            Map<Key<T>, T> keyMap =  brightify.save().entities(entities).now();

            int i =0;
            for(Key<T> key : keyMap.keySet()) {
                refs[i].mKey = key;
                i++;
            }
        } else {
            for(Ref<T> ref : refs) {
//                if(ref.mKey.getId() == null)
            }
        }

        throw new UnsupportedOperationException("Not implemented!");
    }

    public static <T> Ref<T> create(Context context, Key<T> key) {
        return new Ref<T>(context, key);
    }

    public static <T> Ref<T> create(Context context, T entity) {
        Key<T> key = Key.create(entity);
        return new Ref<T>(context, key);
    }


}
