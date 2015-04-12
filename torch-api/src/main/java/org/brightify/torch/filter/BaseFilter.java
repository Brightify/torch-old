package org.brightify.torch.filter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilter<OWNER, TYPE> {

    private final Property<OWNER, TYPE> property;
    private final FilterType filterType;

    private List<OperatorFilterTuple> filters;

    public BaseFilter(Property<OWNER, TYPE> property, FilterType filterType) {
        this.property = property;
        this.filterType = filterType;
    }

    public BaseFilter<OWNER, TYPE> and(BaseFilter<OWNER, ?> filter) {
        addFilter(Operator.AND, filter);
        return this;
    }

    public BaseFilter<OWNER, TYPE> or(BaseFilter<OWNER, ?> filter) {
        addFilter(Operator.OR, filter);
        return this;
    }

    protected FilterType getType() {
        return filterType;
    }

    protected Iterable<OperatorFilterTuple> getFilters() {
        return filters;
    }

    protected Property<OWNER, TYPE> getProperty() {
        return property;
    }

    private void addFilter(Operator operator, BaseFilter filter) {
        if (filters == null) {
            filters = new ArrayList<OperatorFilterTuple>();
        }
        filters.add(new OperatorFilterTuple(operator, filter));
    }

    public static class OperatorFilterTuple {

        private final Operator operator;
        private final BaseFilter filter;

        public OperatorFilterTuple(Operator operator, BaseFilter filter) {
            this.operator = operator;
            this.filter = filter;
        }

        public Operator getOperator() {
            return operator;
        }

        public BaseFilter getFilter() {
            return filter;
        }
    }

    public enum Operator {
        AND, OR
    }

    public enum FilterType {
        CONTAINS_STRING,
        ENDS_WITH_STRING,
        EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL_TO,
        IN,
        LESS_THAN,
        LESS_THAN_OR_EQUAL_TO,
        NOT_EQUAL,
        NOT_IN,
        STARTS_WITH_STRING
    }

}
