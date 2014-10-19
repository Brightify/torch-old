package org.brightify.torch.action.load;

import org.brightify.torch.util.async.Callback;

public interface Countable {

    int count();

    void count(Callback<Integer> callback);

}
