package org.brightify.torch;

import org.brightify.torch.util.Callback;

public interface Result<ENTITY> {

    ENTITY now();

    void async(Callback<ENTITY> callback);
}
