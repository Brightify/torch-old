package com.brightgestures.brightify.sql;

public class SignedNumber implements SqlQueryPart {

    protected int mValue = 0;
    protected Boolean mNegative = null;

    public SignedNumber(int value) {
        setValue(value);
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        if(value < 0) {
            mNegative = true;
        }

        mValue = Math.abs(value);
    }

    public Boolean isNegative() {
        return mNegative;
    }

    public void setNegative(Boolean negative) {
        mNegative = negative;
    }

    @Override
    public void query(StringBuilder builder) {
        if(mNegative != null && mNegative) {
            builder.append("-");
        } else if(mNegative != null) {
            builder.append("+");
        }
        builder.append(mValue);
    }
}