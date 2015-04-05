package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JStatement;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Marshaller {

    boolean accepts(TypeMirror type);

    int getPriority();

    PropertyType getPropertyType();

    JFieldVar createPropertyField(EntityDescriptionGenerator.ClassHolder holder, PropertyMirror propertyMirror)
            throws JClassAlreadyExistsException;

    JStatement marshall(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror);

    JStatement unmarshall(EntityDescriptionGenerator.CreateFromRawEntityHolder holder, PropertyMirror propertyMirror);

    enum PropertyType {
        VALUE, REFERENCE
    }

}