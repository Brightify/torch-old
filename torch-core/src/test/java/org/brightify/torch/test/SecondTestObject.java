package org.brightify.torch.test;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class SecondTestObject {

    @Id
    public Long id;

    public String string;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecondTestObject that = (SecondTestObject) o;
        return Objects.equal(id, that.id) &&
               Objects.equal(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, string);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("string", string)
                          .toString();
    }
}
