package org.brightify.torch.test;

import org.brightify.torch.annotation.Accessor;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Ignore;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.util.MigrationAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Entity(revision = 4)
@Index
public class TestObject implements Serializable {
    public static final int SHOULD_BE_IGNORED = 10000;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestObject.class);
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
    public String group; // Test for #20, should be forbidden column name, but we add torch_ to every column name
    protected String protectedTest = "protectedTest";
    String defaultTest = "defaultTest";
    @Ignore
    public List<String> strings;
    @Ignore
    public SecondTestObject secondTestObject;
    @Ignore
    public List<SecondTestObject> secondTestObjects;
//    public List<String> strings = new ArrayList<String>();

    @Migration(source = 1, target = 2)
    public static void migrate100200(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = 2, target = 3)
    public static void migrate200300(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = 3, target = 4)
    public static void migrate300352(MigrationAssistant<TestObject> assistant) {

    }

    @Migration(source = 1, target = 4)
    public static void migrate100352(MigrationAssistant<TestObject> assistant) {

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestObject that = (TestObject) o;

        if (booleanPrimitiveField != that.booleanPrimitiveField) return false;
        if (intPrimitiveField != that.intPrimitiveField) return false;
        if (longPrimitiveField != that.longPrimitiveField) return false;
        if (booleanField != null ? !booleanField.equals(that.booleanField) : that.booleanField != null) return false;
        if (!Arrays.equals(byteArrayField, that.byteArrayField)) return false;
        if (defaultTest != null ? !defaultTest.equals(that.defaultTest) : that.defaultTest != null) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (intField != null ? !intField.equals(that.intField) : that.intField != null) return false;
        if (longField != null ? !longField.equals(that.longField) : that.longField != null) return false;
        if (protectedTest != null ? !protectedTest.equals(that.protectedTest) : that.protectedTest != null)
            return false;
        if (secondTestObject != null ? !secondTestObject.equals(that.secondTestObject) : that.secondTestObject != null)
            return false;
        if (secondTestObjects != null ? !secondTestObjects.equals(that.secondTestObjects) : that.secondTestObjects !=
                null)
            return false;
        if (stringField != null ? !stringField.equals(that.stringField) : that.stringField != null) return false;

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
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (protectedTest != null ? protectedTest.hashCode() : 0);
        result = 31 * result + (defaultTest != null ? defaultTest.hashCode() : 0);
        result = 31 * result + (secondTestObject != null ? secondTestObject.hashCode() : 0);
        result = 31 * result + (secondTestObjects != null ? secondTestObjects.hashCode() : 0);
        return result;
    }
}
