package org.brightify.torch.compile.generate;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.EntityMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityMetadataGenerator {

    void generate(EntityMirror entityMirror) throws Exception;

    void generate(EntityMirror entityMirror, JDefinedClass definedClass) throws Exception;


    public static class ClassHolder {
        public EntityMirror entityMirror;
        public JClass entityClass;
        public JDefinedClass definedClass;
    }

    public static class CreateFromCursorHolder {
        public ClassHolder classHolder;
        public JMethod method;
        public JVar torch;
        public JVar cursor;
        public JVar loadGroups;
        public JVar entity;
    }

    public static class ToContentValuesHolder {
        public ClassHolder classHolder;
        public JMethod method;
        public JVar torch;
        public JVar contentValues;
        public JVar entity;
    }
}
