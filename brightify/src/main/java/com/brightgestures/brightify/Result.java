package com.brightgestures.brightify;

import com.brightgestures.brightify.util.Callback;

public interface Result<ENTITY> {

    ENTITY now();

    void async(Callback<ENTITY> callback);
}
