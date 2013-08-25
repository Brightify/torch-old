package com.brightgestures.droidorm.sql.affinity;

import com.brightgestures.droidorm.sql.TypeName;

public class NoneAffinity extends TypeName {

    @Override
    public String getName() {
        return "NONE";
    }
}
