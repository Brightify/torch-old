package org.brightify.torch.compile;

import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property<T> {
    public static final String COLUMN_PREFIX = "torch_";

    String getValue();

    String setValue(String value);

    Id getId();

    Index getIndex();

    NotNull getNotNull();

    Unique getUnique();

    String getName();

    String getColumnName();

    TypeMirror getTypeMirror();

    Class<T> getType();

    interface Getter {
        String getValue();

        Element getElement();
    }

    interface Setter {
        String setValue(String value);

        Element getElement();
    }

}
