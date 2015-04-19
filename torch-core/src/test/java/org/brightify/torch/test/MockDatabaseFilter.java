package org.brightify.torch.test;

import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.EnumerationFilter;
import org.brightify.torch.filter.FilterMethodPublicRouter;
import org.brightify.torch.filter.SingleValueFilter;
import org.brightify.torch.util.HashMapBuilder;

import java.util.Map;

public class MockDatabaseFilter {

    private static Map<BaseFilter.FilterType, MockFilterMatcher> operatorMap =
            HashMapBuilder.<BaseFilter.FilterType, MockFilterMatcher>begin()
                          .put(BaseFilter.FilterType.EQUAL, new SingleValueMockFilterMatcher() {
                              @Override
                              protected boolean applyWithValue(Object value, Object filterValue) {
                                  if (value == null) {
                                      return filterValue == null;
                                  } else {
                                      return value.equals(filterValue);
                                  }
                              }
                          })
                          .put(BaseFilter.FilterType.NOT_EQUAL, new SingleValueMockFilterMatcher() {
                              @Override
                              protected boolean applyWithValue(Object value, Object filterValue) {
                                  if (value == null) {
                                      return filterValue != null;
                                  } else {
                                      return !value.equals(filterValue);
                                  }
                              }
                          })
                          .put(BaseFilter.FilterType.GREATER_THAN, new NumberComparationFilterMatcher() {
                              @Override
                              protected boolean compareLong(long value, long filterValue) {
                                  return value > filterValue;
                              }

                              @Override
                              protected boolean compareDouble(double value, double filterValue) {
                                  return value > filterValue;
                              }
                          })
                          .put(BaseFilter.FilterType.GREATER_THAN_OR_EQUAL_TO, new NumberComparationFilterMatcher() {
                              @Override
                              protected boolean compareLong(long value, long filterValue) {
                                  return value >= filterValue;
                              }

                              @Override
                              protected boolean compareDouble(double value, double filterValue) {
                                  return value >= filterValue;
                              }
                          })
                          .put(BaseFilter.FilterType.LESS_THAN, new NumberComparationFilterMatcher() {
                              @Override
                              protected boolean compareLong(long value, long filterValue) {
                                  return value < filterValue;
                              }

                              @Override
                              protected boolean compareDouble(double value, double filterValue) {
                                  return value < filterValue;
                              }
                          })
                          .put(BaseFilter.FilterType.LESS_THAN_OR_EQUAL_TO, new NumberComparationFilterMatcher() {
                              @Override
                              protected boolean compareLong(long value, long filterValue) {
                                  return value <= filterValue;
                              }

                              @Override
                              protected boolean compareDouble(double value, double filterValue) {
                                  return value <= filterValue;
                              }
                          })
                          .put(BaseFilter.FilterType.IN, new EnumerationMockFilterMatcher() {
                              @Override
                              protected boolean applyWithEnumeration(Object value, Iterable<?> filterValues) {
                                  for (Object filterValue : filterValues) {
                                      if (filterValue.equals(value)) {
                                          return true;
                                      }
                                  }
                                  return false;
                              }
                          })
                          .put(BaseFilter.FilterType.NOT_IN, new EnumerationMockFilterMatcher() {
                              @Override
                              protected boolean applyWithEnumeration(Object value, Iterable<?> filterValues) {
                                  for (Object filterValue : filterValues) {
                                      if (filterValue.equals(value)) {
                                          return false;
                                      }
                                  }
                                  return true;
                              }
                          })
                          .put(BaseFilter.FilterType.CONTAINS_STRING, new StringProcessingFilterMatcher() {
                              @Override
                              protected boolean process(String value, String filterValue) {
                                  return value != null && value.contains(filterValue);
                              }
                          })
                          .put(BaseFilter.FilterType.STARTS_WITH_STRING, new StringProcessingFilterMatcher() {
                              @Override
                              protected boolean process(String value, String filterValue) {
                                  return value != null && value.startsWith(filterValue);
                              }
                          })
                          .put(BaseFilter.FilterType.ENDS_WITH_STRING, new StringProcessingFilterMatcher() {
                              @Override
                              protected boolean process(String value, String filterValue) {
                                  return value != null && value.endsWith(filterValue);
                              }
                          })
                          .map();

    public static boolean applyFilter(BaseFilter<?, ?> filter, RawEntity entity) {
        BaseFilter.FilterType filterType = FilterMethodPublicRouter.getFilterType(filter);
        MockFilterMatcher matcher = operatorMap.get(filterType);
        if (matcher == null) {
            throw new IllegalStateException("Unsupported filter " + filterType.name());
        }

        Iterable<BaseFilter.OperatorFilterTuple> filterTuples = FilterMethodPublicRouter.getFilters(filter);

        Object value = entity.getValue(FilterMethodPublicRouter.getProperty(filter).getSafeName());

        boolean applies = matcher.apply(filter, value);

        if (filterTuples != null) {
            for (BaseFilter.OperatorFilterTuple filterTuple : filterTuples) {
                BaseFilter.Operator operator = filterTuple.getOperator();
                BaseFilter<?, ?> nextFilter = filterTuple.getFilter();

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
        boolean apply(BaseFilter<?, ?> filter, Object value);
    }

    private static abstract class SingleValueMockFilterMatcher implements MockFilterMatcher {
        @Override
        public boolean apply(BaseFilter<?, ?> filter, Object value) {
            SingleValueFilter<?, ?> singleValueFilter = (SingleValueFilter<?, ?>) filter;

            return applyWithValue(value, FilterMethodPublicRouter.getSingleValue(singleValueFilter));
        }

        protected abstract boolean applyWithValue(Object value, Object filterValue);
    }

    private static abstract class EnumerationMockFilterMatcher implements MockFilterMatcher {
        @Override
        public boolean apply(BaseFilter<?, ?> filter, Object value) {
            EnumerationFilter<?, ?> enumerationFilter = (EnumerationFilter<?, ?>) filter;

            return applyWithEnumeration(value, FilterMethodPublicRouter.getValueEnumeration(enumerationFilter));
        }

        protected abstract boolean applyWithEnumeration(Object value, Iterable<?> filterValues);
    }

    private static abstract class NumberComparationFilterMatcher extends SingleValueMockFilterMatcher {
        @Override
        protected boolean applyWithValue(Object value, Object filterValue) {
            if (value == null ^ filterValue == null) {
                return false;
            } else if (value == null && filterValue == null) {
                return true;
            }
            Number valueNumber = (Number) value;
            Number filterValueNumber = (Number) filterValue;

            long valueLong = valueNumber.longValue();
            long filterValueLong = filterValueNumber.longValue();
            if (valueLong != filterValueLong) {
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
