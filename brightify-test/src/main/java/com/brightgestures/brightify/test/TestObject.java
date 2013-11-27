package com.brightgestures.brightify.test;

import com.brightgestures.brightify.annotation.Accessor;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.Index;
import com.brightgestures.brightify.annotation.Migration;
import com.brightgestures.brightify.util.MigrationAssistant;

import java.io.Serializable;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
@Entity(version = "3.5.2")
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
    protected String protectedTest = "protectedTest";
    String defaultTest = "defaultTest";

    @Accessor
    public String getProtectedTest() {
        return protectedTest;
    }

    @Accessor
    public void setProtectedTest(String protectedTest) {
        this.protectedTest = protectedTest;
    }

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

    @Migration(source = "1.0.0", target = "2.0.0")
    public static void migrate100200(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = "2.0.0", target = "3.0.0")
    public static void migrate200300(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = "3.0.0", target = "3.5.2")
    public static void migrate300352(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = "1.0.0", target = "3.5.2")
    public static void migrate100352(MigrationAssistant<TestObject> assistant) {

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
