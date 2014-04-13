package org.brightify.torch.compile.filter;

import com.google.inject.Inject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import org.brightify.torch.compile.Property;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.util.Types;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class AbstractColumnProvider implements ColumnProvider {

    private final Class<?>[] acceptedClasses;

    @Inject
    private TypeHelper typeHelper;
    @Inject
    private Types types;

    public AbstractColumnProvider(Class<?>... acceptedClasses) {
        this.acceptedClasses = acceptedClasses;
    }

    @Override
    public boolean accepts(Property property) {
        for (Class<?> acceptedClass : acceptedClasses) {
            if (types.isSameType(typeHelper.typeOf(acceptedClass), typeHelper.getWrappedType(property))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JFieldVar createColumnField(JDefinedClass definedClass, Property property) {
        return definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, columnClass(property), property.getName(),
                                  columnClassInvocation(property));
    }

    protected abstract JClass columnClass(Property property);

    protected abstract JClass columnClassImpl(Property property);

    protected JInvocation columnClassInvocation(Property property) {
        return JExpr._new(columnClassImpl(property)).arg(property.getColumnName());
    }
}
