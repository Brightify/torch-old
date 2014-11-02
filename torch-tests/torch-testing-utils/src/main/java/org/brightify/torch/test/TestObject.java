package org.brightify.torch.test;

import org.brightify.torch.annotation.Accessor;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.util.MigrationAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Entity(version = "3.5.2")
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
//    public Table table;
//    public List<Table> tables;
//    public List<String> strings = new ArrayList<String>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            LOGGER.debug("Types not equal: `{}` vs `{}`", getClass(), o != null ? o.getClass() : "`null`");
            return false;
        }

        TestObject that = (TestObject) o;

        if (booleanPrimitiveField != that.booleanPrimitiveField) {
            LOGGER.debug("`booleanPrimitiveField` not equal: `{}` vs `{}`", booleanPrimitiveField,
                         that.booleanPrimitiveField);
            return false;
        }

        if (intPrimitiveField != that.intPrimitiveField) {
            LOGGER.debug("`intPrimitiveField` not equal: `{}` vs `{}`", intPrimitiveField, that.intPrimitiveField);
            return false;
        }

        if (longPrimitiveField != that.longPrimitiveField) {
            LOGGER.debug("`longPrimitiveField` not equal: `{}` vs `{}`", longPrimitiveField, that.longPrimitiveField);
            return false;
        }

        if (booleanField != null ? !booleanField.equals(that.booleanField) : that.booleanField != null) {
            LOGGER.debug("`booleanField` not equal: {} vs {}", booleanField, that.booleanField);
            return false;
        }

        if (!Arrays.equals(byteArrayField, that.byteArrayField)) {
            LOGGER.debug("`byteArrayField` not equal: `{}` vs `{}`", Arrays.toString(byteArrayField),
                         Arrays.toString(that.byteArrayField));
            return false;
        }

        if (defaultTest != null ? !defaultTest.equals(that.defaultTest) : that.defaultTest != null) {
            LOGGER.debug("`defaultTest` not equal: `{}` vs `{}`", defaultTest, that.defaultTest);
            return false;
        }

        if (group != null ? !group.equals(that.group) : that.group != null) {
            LOGGER.debug("`group` not equal: `{}` vs `{}`", group, that.group);
            return false;
        }

        if (id != null ? !id.equals(that.id) : that.id != null) {
            LOGGER.debug("`id` not equal: `{}` vs `{}`", id, that.id);
            return false;
        }

        if (intField != null ? !intField.equals(that.intField) : that.intField != null) {
            LOGGER.debug("`intField` not equal: `{}` vs `{}`", intField, that.intField);
            return false;
        }

        if (longField != null ? !longField.equals(that.longField) : that.longField != null) {
            LOGGER.debug("`longField` not equal: `{}` vs `{}`", longField, that.longField);
            return false;
        }

        if (protectedTest != null ? !protectedTest.equals(that.protectedTest) : that.protectedTest != null) {
            LOGGER.debug("`protectedTest` not equal: `{}` vs `{}`", protectedTest, that.protectedTest);
            return false;
        }

        if (stringField != null ? !stringField.equals(that.stringField) : that.stringField != null) {
            LOGGER.debug("`stringField` not equal: `{}` vs `{}`", stringField, that.stringField);
            return false;
        }

/*        if (strings != null ? !strings.equals(that.strings) : that.strings != null) {
            LOGGER.debug("`strings` not equal: `{}` vs `{}`", strings.toArray(), that.strings.toArray());
            return false;
        }*/

        return true;
    }
}
