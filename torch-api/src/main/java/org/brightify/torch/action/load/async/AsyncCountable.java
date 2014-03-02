package org.brightify.torch.action.load.async;

import org.brightify.torch.util.Callback;

public interface AsyncCountable {

    void count(Callback<Integer> callback);

}
