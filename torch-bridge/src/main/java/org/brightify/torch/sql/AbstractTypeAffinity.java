package org.brightify.torch.sql;

import org.brightify.torch.sql.TypeAffinity;

public abstract class AbstractTypeAffinity implements TypeAffinity {
    @Override
    public void query(StringBuilder builder) {
        builder.append(getName());
    }
}
