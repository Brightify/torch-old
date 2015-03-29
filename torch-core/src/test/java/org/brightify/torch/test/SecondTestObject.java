package org.brightify.torch.test;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class SecondTestObject {

    @Id
    public Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecondTestObject that = (SecondTestObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
