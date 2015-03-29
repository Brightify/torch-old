package org.brightify.torch.filter;

/**
 * This class is an utility for accessing the protected methods of different filters from external packages. This is
 * because having these methods publicly visible on the class itself wouldn't be good for the fluent API.
 */
public final class FilterMethodPublicRouter {

    public static <TYPE> Property<TYPE> getProperty(BaseFilter<TYPE, ?> filter) {
        return filter.getProperty();
    }

    public static Iterable<BaseFilter.OperatorFilterTuple> getFilters(BaseFilter<?, ?> filter) {
        return filter.getFilters();
    }

    public static <TYPE> TYPE getSingleValue(SingleValueFilter<TYPE, ?> filter) {
        return filter.getValue();
    }

    public static <TYPE> Iterable<TYPE> getValueEnumeration(EnumerationFilter<TYPE, ?> filter) {
        return filter.getValues();
    }

}
