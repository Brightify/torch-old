package com.brightgestures.brightify.test;

import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.Index;

import java.io.Serializable;
import java.util.ArrayList;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
@Entity
@Index
public class ActivityTestObject implements Serializable {
    @Id
    public Long id;

    public Integer intField = 1;
    public String stringField = "testString";
    public Long longField = Long.MAX_VALUE;

    public ArrayList<String> stringList = new ArrayList<String>();

    {
        stringList.add("test1");
        stringList.add("test2");
        stringList.add("test3");
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ActivityTestObject) {
            ActivityTestObject that = (ActivityTestObject) o;
            return ((this.id == null && that.id == null) || (this.id != null && this.id.equals(that.id)))  && this.intField.equals(that.intField) &&
                    this.longField.equals(that.longField) && this.stringField.equals(that.stringField) &&
                    this.stringList.equals(that.stringList);

        }
        return super.equals(o);
    }
}
