package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface GenericProperty<OWNER, TYPE> extends ValueProperty<OWNER, TYPE> {

    BaseFilter<OWNER, TYPE> in(TYPE... values);

    BaseFilter<OWNER, TYPE> in(Iterable<TYPE> values);

    BaseFilter<OWNER, TYPE> notIn(TYPE... values);

    BaseFilter<OWNER, TYPE> notIn(Iterable<TYPE> values);

}
