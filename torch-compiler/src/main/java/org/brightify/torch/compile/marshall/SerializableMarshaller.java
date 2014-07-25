package org.brightify.torch.compile.marshall;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
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
    protected JExpression marshallValue(EntityDescriptionGenerator.ToRawEntityHolder holder, PropertyMirror propertyMirror) {
        JExpression getValue = super.marshallValue(holder, propertyMirror);

        return CodeModelTypes.SERIALIZER.staticInvoke("serialize").arg(getValue);
    }

    @Override
    protected JExpression fromRawEntity(EntityDescriptionGenerator.CreateFromRawEntityHolder holder,
                                     PropertyMirror propertyMirror) {
        JExpression getBlob = holder.rawEntity.invoke("getBlob").arg(JExpr.lit(propertyMirror.getSafeName()));
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
    protected JClass propertyClass(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY;
    }

    @Override
    protected JClass propertyClassImpl(PropertyMirror propertyMirror) {
        return CodeModelTypes.GENERIC_PROPERTY;
    }
}
