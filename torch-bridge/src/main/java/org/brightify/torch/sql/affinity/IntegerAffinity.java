package org.brightify.torch.sql.affinity;

import org.brightify.torch.sql.AbstractTypeAffinity;

public class IntegerAffinity extends AbstractTypeAffinity {
    private static IntegerAffinity instance;

    @Override
    public String getName() {
        return "INTEGER";
    }

    public static IntegerAffinity getInstance() {
        if(instance == null) {
            instance = new IntegerAffinity();
        }
        return instance;
    }
}
