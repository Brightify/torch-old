package com.brightgestures.brightify.sql.clause;

import com.brightgestures.brightify.sql.SqlQueryPart;

public class ConflictClause implements SqlQueryPart {

    protected Action mAction = null;

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    @Override
    public void query(StringBuilder builder) {
        if(mAction != null) {
            builder.append("ON CONFLICT ").append(mAction.name());
        }
    }

    public enum Action {
        ROLLBACK, ABORT, FAIL, IGNORE, REPLACE
    }
}
