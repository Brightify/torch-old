package com.brightgestures.brightify;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class Ref<ENTITY> {
    protected final Brightify mBrightify;

    protected Key<ENTITY> mKey;
    protected ENTITY mValue;
    protected boolean mLoaded = false;

    public Ref(Brightify brightify, Key<ENTITY> key) {
        mBrightify = brightify;
        mKey = key;
    }

    public ENTITY get() {
        if(mValue == null && !mLoaded) {
            mValue = mBrightify.load().key(mKey).now();
            mLoaded = true;
        }
        return mValue;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public static <ENTITY> String refToString(Ref<ENTITY> ref) {
        if(ref != null) {
            return refsToString(ref);
        } else {
            return refsToString();
        }
    }

    public static <ENTITY> String refToString(Brightify brightify, boolean saveValue, Ref<ENTITY> ref) {
        if(ref != null) {
            return refsToString(brightify, saveValue, ref);
        } else {
            return refsToString(brightify, saveValue);
        }
    }

    public static <ENTITY> String refsToString(Ref<ENTITY>... refs) {
        return refsToString(null, false, refs);
    }

    public static <ENTITY> String refsToString(Brightify brightify, boolean saveValues, Ref<ENTITY>... refs) {
        if(saveValues && brightify == null) {
            throw new IllegalArgumentException("Cannot save values without Brightify instance!");
        }
        if(refs.length == 0) {
            return null;
        }

        if(saveValues) {
            LinkedList<ENTITY> entities = new LinkedList<ENTITY>();

            for(Ref<ENTITY> ref : refs) {
                entities.addLast(ref.mValue);
            }

            Map<Key<ENTITY>, ENTITY> keyMap =  brightify.save().entities(entities).now();

            int i =0;
            for(Key<ENTITY> key : keyMap.keySet()) {
                refs[i].mKey = key;
                i++;
            }
        } else {
            for(Ref<ENTITY> ref : refs) {
//                if(ref.mKey.getId() == null)
            }
        }

        throw new UnsupportedOperationException("Not implemented!");
    }


}
