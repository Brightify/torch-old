package com.brightgestures.brightify.test;

import com.brightgestures.brightify.annotation.Accessor;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.Index;

import java.io.Serializable;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
@Entity
@Index
public class TestObject implements Serializable {
    @Id
    public Long id;

    //public byte[] byteArrayField = new byte[1024];

    public Integer intField = 1;
    public int intPrimitiveField = 100;
    public String stringField = "testString";
    public Long longField = Long.MAX_VALUE;
    public long longPrimitiveField = Long.MIN_VALUE;
    public Boolean booleanField = true;
    public boolean booleanPrimitiveField = false;

    @Accessor(name = "testName", type = Accessor.Type.GET)
    public int a() {
        return 23;
    }

    public void setTestName(int testName) {

    }

    @Accessor(type = Accessor.Type.GET, name = "testMethod")
    public long testMethod() {
        return 500;
    }

    @Accessor(type = Accessor.Type.SET, name = "testMethod")
    public void halelujah(long ttttt) {

    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TestObject) {
            TestObject that = (TestObject) o;
            return ((this.id == null && that.id == null) || (this.id != null && this.id.equals(that.id)))  && this.intField.equals(that.intField) &&
                    this.longField.equals(that.longField) && this.stringField.equals(that.stringField);

        }
        return super.equals(o);
    }
}
