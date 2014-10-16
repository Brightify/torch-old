package org.brightify.torch.filter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilter<TYPE, FILTER extends BaseFilter<TYPE, FILTER>> {

    private List<OperatorFilterTuple> filters;
    private final Property<TYPE> property;

    public BaseFilter(Property<TYPE> property) {
        this.property = property;
    }

    public BaseFilter<?, ?> and(BaseFilter<?, ?> filter) {
        addFilter(Operator.AND, filter);
        return this;
    }

    public BaseFilter<?, ?> or(BaseFilter<?, ?> filter) {
        addFilter(Operator.OR, filter);
        return this;
    }

    protected Iterable<OperatorFilterTuple> getFilters() {
        return filters;
    }

    protected Property<TYPE> getProperty() {
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

    public static enum Operator {
        AND, OR
    }

}
