package com.brightgestures.brightify.sql.affinity;

import com.brightgestures.brightify.sql.TypeName;

public class NoneAffinity extends TypeName {

    @Override
    public String getName() {
        return "NONE";
    }
}
