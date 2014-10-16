package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface GenericProperty<TYPE> extends Property<TYPE> {

    InFilter<?> in(TYPE... values);

    NotInFilter<?> notIn(TYPE... values);

}
