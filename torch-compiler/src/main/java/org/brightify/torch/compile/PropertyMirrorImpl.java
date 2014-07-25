package org.brightify.torch.compile;

import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;
import org.brightify.torch.util.Constants;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyMirrorImpl implements PropertyMirror {

    private List<? extends AnnotationMirror> annotations;
    private Id id;
    private Index index;
    private NotNull notNull;
    private Unique unique;
    private String name;
    private String safeName;
    private TypeMirror typeMirror;
    private Getter getter;
    private Setter setter;

    @Override
    public List<? extends AnnotationMirror> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<? extends AnnotationMirror> annotations) {
        this.annotations = annotations;
    }

    @Override
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    @Override
    public NotNull getNotNull() {
        return notNull;
    }

    public void setNotNull(NotNull notNull) {
        this.notNull = notNull;
    }

    @Override
    public Unique getUnique() {
        return unique;
    }

    public void setUnique(Unique unique) {
        this.unique = unique;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(safeName == null) {
            safeName = Constants.SAFETY_PREFIX + name;
        }
    }

    @Override
    public String getSafeName() {
        return safeName;
    }

    public void setSafeName(String safeName) {
        this.safeName = safeName;
    }

    @Override
    public TypeMirror getType() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror type) {
        this.typeMirror = type;
    }

    @Override
    public Getter getGetter() {
        return getter;
    }

    @Override
    public void setGetter(Getter getter) {
        this.getter = getter;
    }

    @Override
    public Setter getSetter() {
        return setter;
    }

    @Override
    public void setSetter(Setter setter) {
        this.setter = setter;
    }

}
