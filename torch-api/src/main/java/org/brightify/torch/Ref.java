package org.brightify.torch;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class Ref<ENTITY> {
    protected final Torch mTorch;

    protected Key<ENTITY> mKey;
    protected ENTITY mValue;
    protected boolean mLoaded = false;

    public Ref(Torch torch, Key<ENTITY> key) {
        mTorch = torch;
        mKey = key;
    }

    public ENTITY get() {
        if(mValue == null && !mLoaded) {
            mValue = mTorch.load().key(mKey);
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

    public static <ENTITY> String refToString(Torch torch, boolean saveValue, Ref<ENTITY> ref) {
        if(ref != null) {
            return refsToString(torch, saveValue, ref);
        } else {
            return refsToString(torch, saveValue);
        }
    }

    public static <ENTITY> String refsToString(Ref<ENTITY>... refs) {
        return refsToString(null, false, refs);
    }

    public static <ENTITY> String refsToString(Torch torch, boolean saveValues, Ref<ENTITY>... refs) {
        if(saveValues && torch == null) {
            throw new IllegalArgumentException("Cannot save values without Torch instance!");
        }
        if(refs.length == 0) {
            return null;
        }

        if(saveValues) {
            LinkedList<ENTITY> entities = new LinkedList<ENTITY>();

            for(Ref<ENTITY> ref : refs) {
                entities.addLast(ref.mValue);
            }

            Map<Key<ENTITY>, ENTITY> keyMap =  torch.save().entities(entities);

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
