package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.sql.TypeAffinity;
import org.brightify.torch.sql.affinity.NoneAffinity;

import java.io.Serializable;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SerializableMarshaller extends AbstractMarshaller {

    public SerializableMarshaller() {
        super(Serializable.class);
    }

    @Override
    public int getPriority() {
        // Lot of objects are serializable, but they may have their custom marshallers so we need to lower the priority
        return Integer.MIN_VALUE;
    }

    @Override
    protected JExpression marshallValue(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror) {
        JExpression getValue = super.marshallValue(holder, propertyMirror);

        return CodeModelTypes.SERIALIZER.staticInvoke("serialize").arg(getValue);
    }

    @Override
    protected JExpression fromCursor(EntityMetadataGenerator.CreateFromCursorHolder holder, JVar index,
                                     PropertyMirror propertyMirror) {
        JExpression getBlob = holder.cursor.invoke("getBlob").arg(index);
        JExpression propertyTypeClass = CodeModelTypes.ref(propertyMirror).dotclass();

        return CodeModelTypes.SERIALIZER.staticInvoke("deserialize").arg(getBlob).arg(propertyTypeClass);
    }

    @Override
    protected boolean nullable(PropertyMirror propertyMirror) {
        return true;
    }

    @Override
    public TypeAffinity getAffinity() {
        return NoneAffinity.getInstance();
    }

    @Override
    protected JClass columnClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY;
    }

    @Override
    protected JClass columnClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY;
    }
}
