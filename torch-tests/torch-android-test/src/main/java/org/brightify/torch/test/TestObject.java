package org.brightify.torch.test;

import android.util.Log;
import org.brightify.torch.annotation.Accessor;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.util.MigrationAssistant;

import java.io.Serializable;
import java.util.Arrays;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
@Entity(version = "3.5.2")
@Index
public class TestObject implements Serializable {
    public static final int SHOULD_BE_IGNORED = 10000;

    @Id
    public Long id;

    public byte[] byteArrayField = new byte[1024];
    public Integer intField = 1;
    public int intPrimitiveField = 100;
    public String stringField = "testString";
    public Long longField = Long.MAX_VALUE;
    public long longPrimitiveField = Long.MIN_VALUE;
    public Boolean booleanField = true;
    public boolean booleanPrimitiveField = false;
    protected String protectedTest = "protectedTest";
    String defaultTest = "defaultTest";
    public String group; // Test for #20, should be forbidden column name, but we add torch_ to every column name
//    public Table table;
//    public List<Table> tables;
//    public List<String> strings = new ArrayList<String>();

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
        if (this == o) {
            Log.d("Equals with sign", "Yep");
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            Log.d("Classes not equal!", "Nope");
            return false;
        }

        TestObject that = (TestObject) o;

        if (booleanPrimitiveField != that.booleanPrimitiveField) {
            Log.d("Not equal", "booleanPrimitiveField: " + booleanPrimitiveField + " vs " + that.booleanPrimitiveField);
            return false;
        }
        Log.d("Equal", "booleanPrimitiveField");

        if (intPrimitiveField != that.intPrimitiveField) {
            Log.d("Not equal", "intPrimitiveField: " + intPrimitiveField + " vs " + that.intPrimitiveField);
            return false;
        }
        Log.d("Equal", "intPrimitiveField");

        if (longPrimitiveField != that.longPrimitiveField) {
            Log.d("Not equal", "longPrimitiveField: " + longPrimitiveField + " vs " + that.longPrimitiveField);
            return false;
        }
        Log.d("Equal", "longPrimitiveField");

        if (booleanField != null ? !booleanField.equals(that.booleanField) : that.booleanField != null) {
            Log.d("Not equal", "booleanField: " + booleanField + " vs " + that.booleanField);
            return false;
        }
        Log.d("Equal", "booleanField");

        if (!Arrays.equals(byteArrayField, that.byteArrayField)) {
            Log.d("Not equal", "byteArrayField: " + Arrays.toString(byteArrayField)+ " vs " + Arrays.toString(that.byteArrayField));
            return false;
        }
        Log.d("Equal", "byteArrayField");

        if (defaultTest != null ? !defaultTest.equals(that.defaultTest) : that.defaultTest != null) {
            Log.d("Not equal", "defaultTest: " + defaultTest + " vs " + that.defaultTest);
            return false;
        }
        Log.d("Equal", "defaultTest");

        if (group != null ? !group.equals(that.group) : that.group != null) {
            Log.d("Not equal", "group: " + group + " vs " + that.group);
            return false;
        }
        Log.d("Equal", "group");

        if (id != null ? !id.equals(that.id) : that.id != null) {
            Log.d("Not equal", "id: " + id + " vs " + that.id);
            return false;
        }
        Log.d("Equal", "id");

        if (intField != null ? !intField.equals(that.intField) : that.intField != null) {
            Log.d("Not equal", "intField: " + intField + " vs " + that.intField);
            return false;
        }
        Log.d("Equal", "intField");

        if (longField != null ? !longField.equals(that.longField) : that.longField != null) {
            Log.d("Not equal", "longField: " + longField + " vs " + that.longField);
            return false;
        }
        Log.d("Equal", "longField");

        if (protectedTest != null ? !protectedTest.equals(that.protectedTest) : that.protectedTest != null) {
            Log.d("Not equal", "protectedTest: " + protectedTest + " vs " + that.protectedTest);
            return false;
        }
        Log.d("Equal", "protectedTest");

        if (stringField != null ? !stringField.equals(that.stringField) : that.stringField != null) {
            Log.d("Not equal", "stringField: " + stringField + " vs " + that.stringField);
            return false;
        }
        Log.d("Equal", "stringField");

/*        if (strings != null ? !strings.equals(that.strings) : that.strings != null) {
            Log.d("Not equal", "strings: " + strings.toArray() + " vs " + that.strings.toArray());
            return false;
        }*/
        Log.d("Equal", "string");

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (byteArrayField != null ? Arrays.hashCode(byteArrayField) : 0);
        result = 31 * result + (intField != null ? intField.hashCode() : 0);
        result = 31 * result + intPrimitiveField;
        result = 31 * result + (stringField != null ? stringField.hashCode() : 0);
        result = 31 * result + (longField != null ? longField.hashCode() : 0);
        result = 31 * result + (int) (longPrimitiveField ^ (longPrimitiveField >>> 32));
        result = 31 * result + (booleanField != null ? booleanField.hashCode() : 0);
        result = 31 * result + (booleanPrimitiveField ? 1 : 0);
        result = 31 * result + (protectedTest != null ? protectedTest.hashCode() : 0);
        result = 31 * result + (defaultTest != null ? defaultTest.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
//        result = 31 * result + (strings != null ? strings.hashCode() : 0);
        return result;
    }
}
