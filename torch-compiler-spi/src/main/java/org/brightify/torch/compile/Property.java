package org.brightify.torch.compile;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Property {
    public static final String COLUMN_PREFIX = "torch_";

    Id getId();

    Index getIndex();

    NotNull getNotNull();

    Unique getUnique();

    String getName();

    String getColumnName();

    TypeMirror getType();

    Getter getGetter();

    void setGetter(Getter getter);

    Setter getSetter();

    void setSetter(Setter setter);

    interface Getter {
        JExpression getValue(JVar entity);

        Element getElement();
    }

    interface Setter {
        JStatement setValue(JVar entity, JExpression value);

        Element getElement();
    }

}
