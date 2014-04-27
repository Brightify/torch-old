package org.brightify.torch.compile.parse;

import com.google.inject.Inject;
import com.sun.codemodel.JAssign;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.annotation.Accessor;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Ignore;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.PropertyMirrorImpl;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.compile.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyParserImpl implements PropertyParser {

    private static final List<String> ignoredMethods = Arrays.asList("equals", "toString");

    @Inject
    private Messager messager;

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private EntityContext entityContext;

    @Override
    public Map<String, PropertyMirror> parseEntityElement(Element element) {
        Map<String, GetSetPair> getSetPairMap = new HashMap<String, GetSetPair>();
        for (Element property : element.getEnclosedElements()) {
            parseGetSetPairs(getSetPairMap, property);
        }


        Map<String, PropertyMirror> propertyMap = new HashMap<String, PropertyMirror>();
        for (GetSetPair pair : getSetPairMap.values()) {
            parsePropertyElement(propertyMap, pair);
        }
        return propertyMap;
    }

    private void parseGetSetPairs(Map<String, GetSetPair> map, Element element) {
        if (shouldIgnore(element)) {
            return;
        }

        Set<Modifier> modifiers = element.getModifiers();
        String name = element.getSimpleName().toString();
        String fullName = element.toString();

        if (modifiers.contains(Modifier.STATIC)) {
            return;
        }

        if (element.getKind() == ElementKind.FIELD) {
            if (modifiers.contains(Modifier.FINAL)) {
                return;
            }

            GetSetPair pair = map.get(name);
            if (pair != null) {
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Property " + name + " ignored because it is already defined.");
                return;
            }
            FieldGetterSetter getterSetter = new FieldGetterSetter(element);
            getterSetter.setName(name);

            pair = new GetSetPair();
            pair.setColumnName(name);
            pair.setGetter(getterSetter);
            pair.setSetter(getterSetter);
            pair.setType(element.asType());

            map.put(name, pair);
        } else if (element.getKind() == ElementKind.METHOD) {
            Accessor accessor = element.getAnnotation(Accessor.class);

            Accessor.Type accessorType = Accessor.Type.INFERRED;
            String columnName = "";
            ExecutableElement executable = (ExecutableElement) element;
            if (accessor != null) {
                columnName = accessor.name();
                accessorType = accessor.type();
                if (accessorType == Accessor.Type.INFERRED) {
                    if (isGetter(executable)) {
                        accessorType = Accessor.Type.GET;
                    } else if (isSetter(executable)) {
                        accessorType = Accessor.Type.SET;
                    } else {
                        throw new EntityParseException(element,
                                                       "Could not infer accessor type! If it is supposed to be a " +
                                                       "getter, it has to have no parameters and non-void return type" +
                                                       ". If it is supposted to be a setter, " +
                                                       "it has to have exacly one parameter and void return type."
                        );
                    }
                }
            }

            if (columnName.equals("")) {
                if (isAccessorName(name, "get")) {
                    columnName = stripAccessorPrefix(name, "get");
                    if (accessorType == Accessor.Type.INFERRED) {
                        accessorType = Accessor.Type.GET;
                    }
                } else if (isAccessorName(name, "set")) {
                    columnName = stripAccessorPrefix(name, "set");
                    if (accessorType == Accessor.Type.INFERRED) {
                        accessorType = Accessor.Type.SET;
                    }
                } else if (isAccessorName(name, "is")) {
                    columnName = stripAccessorPrefix(name, "is");
                    if (accessorType == Accessor.Type.INFERRED) {
                        accessorType = Accessor.Type.GET;
                    }
                } else if (accessorType != Accessor.Type.INFERRED) {
                    columnName = name;
                } else {
                    // FIXME should we log an NOTE message that we skipped it?
                    return;
                }
            }


            GetSetPair pair = map.get(columnName);
            boolean fieldPair = pair != null &&
                                pair.getter() == pair.setter() &&
                                pair.getter() instanceof FieldGetterSetter;

            if (pair != null && !fieldPair) {
                String dupliciteAccessorMessage = "%s %s for property %s was already defined " +
                                                  "elsewhere! Your entity cannot have multiple accessors of the " +
                                                  "same type for a single property.";
                if (accessorType == Accessor.Type.GET && pair.getter() != null) {
                    throw new EntityParseException(element, dupliciteAccessorMessage, "Getter", name, columnName);
                } else if (accessorType == Accessor.Type.SET && pair.setter() != null) {
                    throw new EntityParseException(element, dupliciteAccessorMessage, "Setter", name, columnName);
                }
            } else if (pair == null || accessor != null) {
                pair = new GetSetPair();
                pair.setColumnName(columnName);
                map.put(columnName, pair);
            } else {
                messager.printMessage(Diagnostic.Kind.NOTE, "Accessor " + name + " for property " + columnName +
                                                            " ignored because the property was already defined as" +
                                                            " a field. If you want to force usage of this " +
                                                            "accessor, either make the field private or annotate " +
                                                            "the accessor with @Accessor.", element);
                return;
            }

            if (accessorType == Accessor.Type.GET) {
                if (executable.getParameters().size() != 0) {
                    throw new EntityParseException(element, "Getter %s must not have any parameters!", fullName);
                }
                if (executable.getThrownTypes().size() != 0) {
                    throw new EntityParseException(element, "Getter %s must not throw any exceptions!", fullName);
                }
                if (pair.getType() == null) {
                    pair.setType(executable.getReturnType());
                }

                assureType(element, pair, executable.getReturnType());

                AccessorGetter getter = new AccessorGetter(element);
                getter.setName(name);

                pair.setGetter(getter);
            } else if (accessorType == Accessor.Type.SET) {
                if (executable.getParameters().size() != 1) {
                    throw new EntityParseException(element, "Setter %s has to have exactly one parameter!", fullName);
                }
                if (executable.getThrownTypes().size() != 0) {
                    throw new EntityParseException(element, "Setter %s must not throw any exceptions!", fullName);
                }

                VariableElement parameter = executable.getParameters().get(0);
                if (pair.getType() == null) {
                    pair.setType(parameter.asType());
                }

                assureType(element, pair, parameter.asType());

                AccessorSetter setter = new AccessorSetter(element);
                setter.setName(name);

                pair.setSetter(setter);
            } else {
                throw new IllegalStateException(
                        "Reached branch that should never be reached. Please report this as a major bug!");
            }

        }


    }

    private void assureType(Element element, GetSetPair pair, TypeMirror type) {
        if (!types.isSameType(pair.getType(), type)) {
            throw new EntityParseException(element,
                                           "Getter's return type and setter's parameter types do not match " +
                                           "for property %s! Stored type: %s, other type: %s",
                                           pair.getColumnName(), pair.getType(), type
            );
        }
    }

    private boolean isAccessorName(String name, String prefix) {
        return name.startsWith(prefix) && Character.isUpperCase(name.codePointAt(prefix.length()));
    }

    private String stripAccessorPrefix(String name, String prefix) {
        return name.substring(prefix.length(), prefix.length() + 1).toLowerCase() + name.substring(prefix.length() + 1);
    }

    private boolean isGetter(ExecutableElement element) {
        return !types.isSameType(element.getReturnType(), types.getNoType(TypeKind.VOID)) &&
               element.getParameters().size() == 0;
    }

    private boolean isSetter(ExecutableElement element) {
        return types.isSameType(element.getReturnType(), types.getNoType(TypeKind.VOID)) &&
               element.getParameters().size() == 1;
    }

    private void parsePropertyElement(Map<String, PropertyMirror> propertyMap, GetSetPair pair) {
        // We take all annotations only from getter
        Element element = pair.getter().getElement();
        Set<Modifier> modifiers = element.getModifiers();
        String name = pair.getColumnName();

        Index index = element.getAnnotation(Index.class);
        Id id = element.getAnnotation(Id.class);
        NotNull notNull = element.getAnnotation(NotNull.class);
        Unique unique = element.getAnnotation(Unique.class);

        TypeMirror typeMirror = pair.getType();

        PropertyMirrorImpl property = new PropertyMirrorImpl();
        property.setId(id);
        property.setIndex(index);
        property.setNotNull(notNull);
        property.setUnique(unique);
        property.setName(name);
        property.setTypeMirror(typeMirror);
        property.setGetter(pair.getter());
        property.setSetter(pair.setter());

        propertyMap.put(name, property);

        if (element.getKind() == ElementKind.FIELD) {

        } else {

        }

    }

    private boolean shouldIgnore(Element element) {
        String name = element.getSimpleName().toString();
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Element " + name +
                                                        " ignored because it is private. Torch do not support private" +
                                                        " elements because it cannot access them.", element);
            return true;
        }
        if (element.getAnnotation(Ignore.class) != null) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Element " + name +
                                  " ignored because it was marked with @Ignore annotation.", element
            );
            return true;
        }

        return false;
    }

    public static class GetSetPair {

        private TypeMirror type;
        private String columnName;
        private PropertyMirror.Getter getter;
        private PropertyMirror.Setter setter;

        public TypeMirror getType() {
            return type;
        }

        public void setType(TypeMirror type) {
            this.type = type;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public PropertyMirror.Getter getter() {
            return getter;
        }

        public void setGetter(PropertyMirror.Getter getter) {
            this.getter = getter;
        }

        public PropertyMirror.Setter setter() {
            return setter;
        }

        public void setSetter(PropertyMirror.Setter setter) {
            this.setter = setter;
        }
    }

    public static class FieldGetterSetter implements PropertyMirror.Getter, PropertyMirror.Setter {

        private final Element element;
        private String name;

        public FieldGetterSetter(Element element) {
            this.element = element;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public JExpression getValue(JVar entity) {
            return entity.ref(getName());
        }

        @Override
        public JStatement setValue(JVar entity, JExpression value) {
            return JAssign.assign(entity.ref(getName()), value);
        }

        @Override
        public Element getElement() {
            return element;
        }
    }

    public static class AccessorGetter implements PropertyMirror.Getter {

        private final Element element;
        private String name;

        public AccessorGetter(Element element) {
            this.element = element;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public JExpression getValue(JVar entity) {
            return entity.invoke(getName());
        }

        @Override
        public Element getElement() {
            return element;
        }
    }

    public static class AccessorSetter implements PropertyMirror.Setter {

        private final Element element;
        private String name;

        public AccessorSetter(Element element){
            this.element = element;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public JStatement setValue(JVar entity, JExpression value) {
            return entity.invoke(getName()).arg(value);
        }

        @Override
        public Element getElement() {
            return element;
        }
    }
}
