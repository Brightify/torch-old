package org.brightify.torch;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.action.delete.Deleter;
import org.brightify.torch.action.delete.DeleterImpl;
import org.brightify.torch.action.load.sync.Loader;
import org.brightify.torch.action.load.LoaderImpl;
import org.brightify.torch.action.save.Saver;
import org.brightify.torch.action.save.SaverImpl;

public class TorchImpl implements Torch {

    protected final TorchFactory mFactory;

    // TODO should we save mSaver and mLoader?

    public TorchImpl(TorchFactory factory) {
        mFactory = factory;
    }

    @Override
    public TorchFactory getFactory() {
        return mFactory;
    }

    @Override
    public Deleter delete() {
        return new DeleterImpl(this);
    }

    @Override
    public Loader load() {
        return new LoaderImpl(this);
    }

    @Override
    public Saver save() {
        return new SaverImpl(this);
    }
}
