package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityGeneratorImpl implements EntityGenerator {

    @Inject
    private TypeHelper typeHelper;

    @Override
    public void generate(EntityMirror entityMirror) throws Exception {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        JDefinedClass definedClass = codeModel._class(entityMirror.getFullName());

        generate(entityMirror, definedClass);
    }

    @Override
    public void generate(EntityMirror entityMirror, JDefinedClass definedClass) throws Exception {
        JAnnotationUse annotationUse = definedClass.annotate(Entity.class);

        for (Property property : entityMirror.getProperties()) {
            Class<?> type = typeHelper.classOf(property.getType());

            JFieldVar field =  definedClass.field(JMod.PRIVATE, type, property.getName());

            JMethod getterMethod = definedClass.method(JMod.PUBLIC, type, "get" + uppercaseFirstLetter(property.getName()));
            getterMethod.body()._return(field);

            JMethod setterMethod = definedClass.method(JMod.PUBLIC, Void.TYPE, "set" + uppercaseFirstLetter(property.getName()));
            JVar setterParameter = setterMethod.param(type, property.getName());
            setterMethod.body().assign(JExpr._this().ref(field), setterParameter);

            // FIXME add some more dynamic way to set annotations
            if(property.getId() != null) {
                getterMethod.annotate(property.getId().annotationType());
            }
            if(property.getIndex() != null) {
                getterMethod.annotate(property.getIndex().annotationType());
            }
            if(property.getNotNull() != null) {
                getterMethod.annotate(property.getNotNull().annotationType());
            }
            if(property.getUnique() != null) {
                getterMethod.annotate(property.getUnique().annotationType());
            }
        }
    }

    private String uppercaseFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
