package org.brightify.torch.sql.affinity;

import org.brightify.torch.sql.AbstractTypeAffinity;

public class RealAffinity extends AbstractTypeAffinity {

    private static RealAffinity instance;

    @Override
    public String getName() {
        return "REAL";
    }

    public static RealAffinity getInstance() {
        if(instance == null) {
            instance = new RealAffinity();
        }
        return instance;
    }
}
