package com.brightgestures.brightify.util;

public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(Exception e);

}
