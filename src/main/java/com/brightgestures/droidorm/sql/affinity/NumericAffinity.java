package com.brightgestures.droidorm.sql.affinity;

import com.brightgestures.droidorm.sql.TypeName;

public class NumericAffinity extends TypeName {

    @Override
    public String getName() {
        return "NUMERIC";
    }

}
