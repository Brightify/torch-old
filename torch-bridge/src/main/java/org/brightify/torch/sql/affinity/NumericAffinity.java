package org.brightify.torch.sql.affinity;

import org.brightify.torch.sql.AbstractTypeAffinity;

public class NumericAffinity extends AbstractTypeAffinity {

    private static NumericAffinity instance;

    @Override
    public String getName() {
        return "NUMERIC";
    }

    public static NumericAffinity getInstance() {
        if(instance == null) {
            instance = new NumericAffinity();
        }
        return instance;
    }
}
