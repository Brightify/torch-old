package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface GenericProperty<OWNER, TYPE> extends ValueProperty<OWNER, TYPE> {

    InFilter<OWNER, TYPE> in(TYPE... values);

    InFilter<OWNER, TYPE> in(Iterable<TYPE> values);

    NotInFilter<OWNER, TYPE> notIn(TYPE... values);

    NotInFilter<OWNER, TYPE> notIn(Iterable<TYPE> values);

}
