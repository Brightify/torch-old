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
import org.brightify.torch.compile.PropertyMirror;
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
        generate(entityMirror, true);
    }

    @Override
    public void generate(EntityMirror entityMirror, boolean annotations) throws Exception {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        JDefinedClass definedClass = codeModel._class(entityMirror.getFullName());

        generate(entityMirror, definedClass);
    }

    @Override
    public void generate(EntityMirror entityMirror, JDefinedClass definedClass) throws Exception {
        generate(entityMirror, definedClass, true);
    }

    @Override
    public void generate(EntityMirror entityMirror, JDefinedClass definedClass, boolean annotations) throws Exception {
        if(annotations) {
            JAnnotationUse annotationUse = definedClass.annotate(Entity.class);
        }

        for (PropertyMirror propertyMirror : entityMirror.getProperties()) {
            Class<?> type = typeHelper.classOf(propertyMirror.getType());

            JFieldVar field =  definedClass.field(JMod.PRIVATE, type, propertyMirror.getName());

            JMethod getterMethod = definedClass.method(JMod.PUBLIC, type, "get" + uppercaseFirstLetter(propertyMirror.getName()));
            getterMethod.body()._return(field);

            JMethod setterMethod = definedClass.method(JMod.PUBLIC, Void.TYPE, "set" + uppercaseFirstLetter(
                    propertyMirror.getName()));
            JVar setterParameter = setterMethod.param(type, propertyMirror.getName());
            setterMethod.body().assign(JExpr._this().ref(field), setterParameter);

            if(annotations) {
                // FIXME add some more dynamic way to set annotations
                if (propertyMirror.getId() != null) {
                    getterMethod.annotate(propertyMirror.getId().annotationType());
                }
                if (propertyMirror.getIndex() != null) {
                    getterMethod.annotate(propertyMirror.getIndex().annotationType());
                }
                if (propertyMirror.getNotNull() != null) {
                    getterMethod.annotate(propertyMirror.getNotNull().annotationType());
                }
                if (propertyMirror.getUnique() != null) {
                    getterMethod.annotate(propertyMirror.getUnique().annotationType());
                }
            }
        }
    }

    private String uppercaseFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
