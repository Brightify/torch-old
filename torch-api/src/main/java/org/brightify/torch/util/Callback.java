package org.brightify.torch.util;

public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(Exception e);

}
