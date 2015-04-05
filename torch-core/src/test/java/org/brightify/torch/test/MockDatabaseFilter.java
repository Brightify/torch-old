package org.brightify.torch.test;

import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.ContainsStringFilter;
import org.brightify.torch.filter.EndsWithStringFilter;
import org.brightify.torch.filter.EnumerationFilter;
import org.brightify.torch.filter.EqualToFilter;
import org.brightify.torch.filter.FilterMethodPublicRouter;
import org.brightify.torch.filter.GreaterThanFilter;
import org.brightify.torch.filter.GreaterThanOrEqualToFilter;
import org.brightify.torch.filter.InFilter;
import org.brightify.torch.filter.LessThanFilter;
import org.brightify.torch.filter.LessThanOrEqualToFilter;
import org.brightify.torch.filter.NotEqualToFilter;
import org.brightify.torch.filter.NotInFilter;
import org.brightify.torch.filter.SingleValueFilter;
import org.brightify.torch.filter.StartsWithStringFilter;

import java.util.HashMap;
import java.util.Map;

public class MockDatabaseFilter {

    private static Map<Class<?>, MockFilterMatcher> operatorMap = new HashMap<Class<?>, MockFilterMatcher>();

    static {
        operatorMap.put(EqualToFilter.class, new SingleValueMockFilterMatcher() {

            @Override
            protected boolean applyWithValue(Object value, Object filterValue) {
                if(value == null) {
                    return filterValue == null;
                } else {
                    return value.equals(filterValue);
                }
            }
        });
        operatorMap.put(NotEqualToFilter.class, new SingleValueMockFilterMatcher() {
            @Override
            protected boolean applyWithValue(Object value, Object filterValue) {
                if(value == null) {
                    return filterValue != null;
                } else {
                    return !value.equals(filterValue);
                }
            }
        });
        operatorMap.put(GreaterThanFilter.class, new NumberComparationFilterMatcher() {
            @Override
            protected boolean compareLong(long value, long filterValue) {
                return value > filterValue;
            }

            @Override
            protected boolean compareDouble(double value, double filterValue) {
                return value > filterValue;
            }
        });
        operatorMap.put(GreaterThanOrEqualToFilter.class, new NumberComparationFilterMatcher() {
            @Override
            protected boolean compareLong(long value, long filterValue) {
                return value >= filterValue;
            }

            @Override
            protected boolean compareDouble(double value, double filterValue) {
                return value >= filterValue;
            }
        });
        operatorMap.put(LessThanFilter.class, new NumberComparationFilterMatcher() {
            @Override
            protected boolean compareLong(long value, long filterValue) {
                return value < filterValue;
            }

            @Override
            protected boolean compareDouble(double value, double filterValue) {
                return value < filterValue;
            }
        });
        operatorMap.put(LessThanOrEqualToFilter.class, new NumberComparationFilterMatcher() {
            @Override
            protected boolean compareLong(long value, long filterValue) {
                return value <= filterValue;
            }

            @Override
            protected boolean compareDouble(double value, double filterValue) {
                return value <= filterValue;
            }
        });
        operatorMap.put(InFilter.class, new EnumerationMockFilterMatcher() {
            @Override
            protected boolean applyWithEnumeration(Object value, Iterable<?> filterValues) {
                for (Object filterValue : filterValues) {
                    if(filterValue.equals(value)) {
                        return true;
                    }
                }
                return false;
            }
        });
        operatorMap.put(NotInFilter.class, new EnumerationMockFilterMatcher() {
            @Override
            protected boolean applyWithEnumeration(Object value, Iterable<?> filterValues) {
                for (Object filterValue : filterValues) {
                    if(filterValue.equals(value)) {
                        return false;
                    }
                }
                return true;
            }
        });
        operatorMap.put(ContainsStringFilter.class, new StringProcessingFilterMatcher() {
            @Override
            protected boolean process(String value, String filterValue) {
                return value != null && value.contains(filterValue);
            }
        });
        operatorMap.put(StartsWithStringFilter.class, new StringProcessingFilterMatcher() {
            @Override
            protected boolean process(String value, String filterValue) {
                return value != null && value.startsWith(filterValue);
            }
        });
        operatorMap.put(EndsWithStringFilter.class, new StringProcessingFilterMatcher() {
            @Override
            protected boolean process(String value, String filterValue) {
                return value != null && value.endsWith(filterValue);
            }
        });
    }

    public static boolean applyFilter(BaseFilter<?, ?, ?> filter, MockDatabaseEngine.RawEntity entity) {
        Class<?> filterType = filter.getClass();
        MockFilterMatcher matcher = operatorMap.get(filterType);
        if (matcher == null) {
            throw new IllegalStateException("Unsupported filter! New filters cannot be added! Filter: " +
                    filter.getClass().getSimpleName());
        }

        Iterable<BaseFilter.OperatorFilterTuple> filterTuples = FilterMethodPublicRouter.getFilters(filter);

        Object value = entity.getValue(FilterMethodPublicRouter.getProperty(filter).getSafeName());

        boolean applies = matcher.apply(filter, value);

        if (filterTuples != null) {
            for (BaseFilter.OperatorFilterTuple filterTuple : filterTuples) {
                BaseFilter.Operator operator = filterTuple.getOperator();
                BaseFilter<?, ?, ?> nextFilter = filterTuple.getFilter();

                boolean nextFilterApplies = applyFilter(nextFilter, entity);
                switch (operator) {
                    case AND:
                        applies &= nextFilterApplies;
                        break;
                    case OR:
                        applies |= nextFilterApplies;
                        break;
                    default:
                        throw new IllegalStateException("Unsupported operator! New operators cannot be added!");
                }
            }
        }

        return applies;
    }

    private interface MockFilterMatcher {
        boolean apply(BaseFilter<?, ?, ?> filter, Object value);
    }

    private static abstract class SingleValueMockFilterMatcher implements MockFilterMatcher {
        @Override
        public boolean apply(BaseFilter<?, ?, ?> filter, Object value) {
            SingleValueFilter<?, ?, ?> singleValueFilter = (SingleValueFilter<?, ?, ?>) filter;

            return applyWithValue(value, FilterMethodPublicRouter.getSingleValue(singleValueFilter));
        }

        protected abstract boolean applyWithValue(Object value, Object filterValue);
    }

    private static abstract class EnumerationMockFilterMatcher implements MockFilterMatcher {
        @Override
        public boolean apply(BaseFilter<?, ?, ?> filter, Object value) {
            EnumerationFilter<?, ?, ?> enumerationFilter = (EnumerationFilter<?, ?, ?>) filter;

            return applyWithEnumeration(value, FilterMethodPublicRouter.getValueEnumeration(enumerationFilter));
        }

        protected abstract boolean applyWithEnumeration(Object value, Iterable<?> filterValues);
    }

    private static abstract class NumberComparationFilterMatcher extends SingleValueMockFilterMatcher {
        @Override
        protected boolean applyWithValue(Object value, Object filterValue) {
            if(value == null ^ filterValue == null) {
                return false;
            } else if (value == null && filterValue == null) {
                return true;
            }
            Number valueNumber = (Number) value;
            Number filterValueNumber = (Number) filterValue;

            long valueLong = valueNumber.longValue();
            long filterValueLong = filterValueNumber.longValue();
            if(valueLong != filterValueLong) {
                return compareLong(valueLong, filterValueLong);
            }

            return compareDouble(valueNumber.doubleValue(), filterValueNumber.doubleValue());
        }

        protected abstract boolean compareLong(long value, long filterValue);

        protected abstract boolean compareDouble(double value, double filterValue);
    }

    private static abstract class StringProcessingFilterMatcher extends SingleValueMockFilterMatcher {
        @Override
        protected boolean applyWithValue(Object value, Object filterValue) {
            String valueString = (String) value;
            String filterValueString = (String) filterValue;

            return process(valueString, filterValueString);
        }

        protected abstract boolean process(String value, String filterValue);
    }

}
