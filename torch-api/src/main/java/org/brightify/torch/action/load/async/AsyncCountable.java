package org.brightify.torch.action.load.async;

import org.brightify.torch.util.async.Callback;

public interface AsyncCountable {

    void count(Callback<Integer> callback);

}
