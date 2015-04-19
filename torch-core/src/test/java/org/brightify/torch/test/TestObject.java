package org.brightify.torch.test;

import com.google.common.base.MoreObjects;
import org.brightify.torch.Ref;
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
import java.util.Collection;
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

    public Ref<SecondTestObject> secondTestObject;

    public Collection<SecondTestObject> secondTestObjects;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestObject object = (TestObject) o;
        return com.google.common.base.Objects.equal(intPrimitiveField, object.intPrimitiveField) &&
               com.google.common.base.Objects.equal(longPrimitiveField, object.longPrimitiveField) &&
               com.google.common.base.Objects.equal(booleanPrimitiveField, object.booleanPrimitiveField) &&
               com.google.common.base.Objects.equal(id, object.id) &&
               com.google.common.base.Objects.equal(byteArrayField, object.byteArrayField) &&
               com.google.common.base.Objects.equal(intField, object.intField) &&
               com.google.common.base.Objects.equal(stringField, object.stringField) &&
               com.google.common.base.Objects.equal(longField, object.longField) &&
               com.google.common.base.Objects.equal(booleanField, object.booleanField) &&
               com.google.common.base.Objects.equal(group, object.group) &&
               com.google.common.base.Objects.equal(protectedTest, object.protectedTest) &&
               com.google.common.base.Objects.equal(defaultTest, object.defaultTest) &&
               com.google.common.base.Objects.equal(strings, object.strings) &&
               com.google.common.base.Objects.equal(secondTestObject, object.secondTestObject) &&
               com.google.common.base.Objects.equal(secondTestObjects, object.secondTestObjects);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(id, byteArrayField, intField, intPrimitiveField, stringField,
                                                       longField, longPrimitiveField,
                                                       booleanField, booleanPrimitiveField, group, protectedTest,
                                                       defaultTest, strings,
                                                       secondTestObject, secondTestObjects);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                      .add("id", id)
                      .add("byteArrayField", byteArrayField)
                      .add("intField", intField)
                      .add("intPrimitiveField", intPrimitiveField)
                      .add("stringField", stringField)
                      .add("longField", longField)
                      .add("longPrimitiveField", longPrimitiveField)
                      .add("booleanField", booleanField)
                      .add("booleanPrimitiveField", booleanPrimitiveField)
                      .add("group", group)
                      .add("protectedTest", protectedTest)
                      .add("defaultTest", defaultTest)
                      .add("strings", strings)
                      .add("secondTestObject", secondTestObject)
                      .add("secondTestObjects", secondTestObjects)
                      .toString();
    }
}
