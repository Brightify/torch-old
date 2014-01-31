package org.brightify.torch.sql.affinity;

import org.brightify.torch.sql.AbstractTypeAffinity;

public class TextAffinity extends AbstractTypeAffinity {

    private static TextAffinity instance;

    @Override
    public String getName() {
        return "TEXT";
    }

    public static TextAffinity getInstance() {
        if(instance == null) {
            instance = new TextAffinity();
        }
        return instance;
    }
}
