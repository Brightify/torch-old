package org.brightify.torch.filter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilter<OWNER, TYPE, FILTER extends BaseFilter<OWNER, TYPE, FILTER>> {

    private List<OperatorFilterTuple> filters;
    private final Property<OWNER, TYPE> property;

    public BaseFilter(Property<OWNER, TYPE> property) {
        this.property = property;
    }

    public BaseFilter<OWNER, ?, ?> and(BaseFilter<OWNER, ?, ?> filter) {
        addFilter(Operator.AND, filter);
        return this;
    }

    public BaseFilter<OWNER, ?, ?> or(BaseFilter<OWNER, ?, ?> filter) {
        addFilter(Operator.OR, filter);
        return this;
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

}
