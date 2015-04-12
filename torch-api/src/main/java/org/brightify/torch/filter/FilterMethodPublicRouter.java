package org.brightify.torch.filter;

/**
 * This class is an utility for accessing the protected methods of different filters from external packages. This is
 * because having these methods publicly visible on the class itself wouldn't be good for the fluent API.
 */
public final class FilterMethodPublicRouter {

    public static BaseFilter.FilterType getFilterType(BaseFilter<?, ?> filter) {
        return filter.getType();
    }

    public static <OWNER, TYPE> Property<OWNER, TYPE> getProperty(BaseFilter<OWNER, TYPE> filter) {
        return filter.getProperty();
    }

    public static <OWNER> Iterable<BaseFilter.OperatorFilterTuple> getFilters(BaseFilter<OWNER, ?> filter) {
        return filter.getFilters();
    }

    public static <OWNER, TYPE> TYPE getSingleValue(SingleValueFilter<OWNER, TYPE> filter) {
        return filter.getValue();
    }

    public static <OWNER, TYPE> Iterable<TYPE> getValueEnumeration(EnumerationFilter<OWNER, TYPE> filter) {
        return filter.getValues();
    }

}
