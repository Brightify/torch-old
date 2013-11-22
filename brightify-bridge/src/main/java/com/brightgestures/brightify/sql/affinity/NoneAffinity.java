package com.brightgestures.brightify.sql.affinity;

import com.brightgestures.brightify.sql.AbstractTypeAffinity;

public class NoneAffinity extends AbstractTypeAffinity {

    private static NoneAffinity instance;

    @Override
    public String getName() {
        return "NONE";
    }

    public static NoneAffinity getInstance() {
        if(instance == null) {
            instance = new NoneAffinity();
        }
        return instance;
    }
}
