package org.brightify.torch.util.async;

public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(Exception e);

}
