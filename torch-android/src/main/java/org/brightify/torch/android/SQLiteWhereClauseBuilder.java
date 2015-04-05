package org.brightify.torch.android;

import android.util.Log;
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
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.SingleValueFilter;
import org.brightify.torch.filter.StartsWithStringFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteWhereClauseBuilder {

    private static final String TAG = SQLiteWhereClauseBuilder.class.getSimpleName();

    private static Map<Class<?>, FilterToSQLConvertor> operatorMap = new HashMap<Class<?>, FilterToSQLConvertor>();

    static {
        operatorMap.put(EqualToFilter.class, new SingleValueFilterToSQLConvertor("="));
        operatorMap.put(NotEqualToFilter.class, new SingleValueFilterToSQLConvertor("!="));
        operatorMap.put(GreaterThanFilter.class, new SingleValueFilterToSQLConvertor(">"));
        operatorMap.put(GreaterThanOrEqualToFilter.class, new SingleValueFilterToSQLConvertor(">="));
        operatorMap.put(LessThanFilter.class, new SingleValueFilterToSQLConvertor("<"));
        operatorMap.put(LessThanOrEqualToFilter.class, new SingleValueFilterToSQLConvertor("<="));

        operatorMap.put(InFilter.class, new EnumerationFilterToSQLConvertor("IN"));
        operatorMap.put(NotInFilter.class, new EnumerationFilterToSQLConvertor("NOT IN"));

        operatorMap.put(StartsWithStringFilter.class, new StringProcessingFilterToSQLConvertor() {
            @Override
            protected String alterValue(String original) {
                return original + "%";
            }
        });
        operatorMap.put(EndsWithStringFilter.class, new StringProcessingFilterToSQLConvertor() {
            @Override
            protected String alterValue(String original) {
                return "%" + original;
            }
        });
        operatorMap.put(ContainsStringFilter.class, new StringProcessingFilterToSQLConvertor() {
            @Override
            protected String alterValue(String original) {
                return "%" + original + "%";
            }
        });
    }

    public static void appendFilter(StringBuilder builder, BaseFilter<?, ?, ?> filter, List<String> selectionArgs) {
        Class<?> filterType = filter.getClass();
        FilterToSQLConvertor convertor = operatorMap.get(filterType);
        if (convertor == null) {
            throw new IllegalStateException("Unsupported filter! New filters cannot be added!");
        }

        Iterable<BaseFilter.OperatorFilterTuple> filterTuples = FilterMethodPublicRouter.getFilters(filter);
        if (filterTuples != null) {
            builder.append("(");
        }

        convertor.convert(builder, filter, selectionArgs);

        if (filterTuples != null) {
            for (BaseFilter.OperatorFilterTuple filterTuple : filterTuples) {
                BaseFilter.Operator operator = filterTuple.getOperator();
                BaseFilter<?, ?, ?> nextFilter = filterTuple.getFilter();

                switch (operator) {
                    case AND:
                        builder.append(" AND ");
                        break;
                    case OR:
                        builder.append(" OR ");
                        break;
                    default:
                        throw new IllegalStateException("Unsupported operator! New operators cannot be added!");
                }

                appendFilter(builder, nextFilter, selectionArgs);
            }

            builder.append(")");
        }
    }


    private interface FilterToSQLConvertor {
        void convert(StringBuilder builder, BaseFilter<?, ?, ?> filter, List<String> selectionArgs);
    }

    private static class SingleValueFilterToSQLConvertor implements FilterToSQLConvertor {

        private final String operator;

        public SingleValueFilterToSQLConvertor(String operator) {
            this.operator = operator;
        }

        @Override
        public void convert(StringBuilder builder, BaseFilter<?, ?, ?> filter, List<String> selectionArgs) {
            if (filter instanceof SingleValueFilter<?, ?, ?>) {
                SingleValueFilter<?, ?, ?> singleValueFilter = (SingleValueFilter<?, ?, ?>) filter;
                Property<?, ?> property = FilterMethodPublicRouter.getProperty(filter);
                Object value = FilterMethodPublicRouter.getSingleValue(singleValueFilter);
                builder.append(" ").append(property.getSafeName()).append(" ").append(operator).append(" ? ");

                // FIXME this conversion should happen elsewhere!
                if (property.getType() == Boolean.class) {
                    Boolean booleanValue = (Boolean) value;
                    selectionArgs.add(booleanValue != null && booleanValue ? "1" : "0");
                } else {
                    selectionArgs.add(value.toString());
                }

            } else {
                Log.w(TAG, "Filter ignored, because it has a wrong binding! Expected " +
                        SingleValueFilter.class.getSimpleName() + " but got " + filter.getClass().getSimpleName());
            }
        }
    }

    private abstract static class StringProcessingFilterToSQLConvertor implements FilterToSQLConvertor {
        @Override
        public void convert(StringBuilder builder, BaseFilter<?, ?, ?> filter, List<String> selectionArgs) {
            if (filter instanceof SingleValueFilter<?, ?, ?>) {
                SingleValueFilter<?, ?, ?> singleValueFilter = (SingleValueFilter<?, ?, ?>) filter;
                Property<?, ?> property = FilterMethodPublicRouter.getProperty(filter);
                Object value = FilterMethodPublicRouter.getSingleValue(singleValueFilter);
                builder.append(" ").append(property.getSafeName()).append(" LIKE ? ");

                selectionArgs.add(alterValue(value.toString()));
            } else {
                Log.w(TAG, "Filter ignored, because it has a wrong binding! Expected " +
                        SingleValueFilter.class.getSimpleName() + " but got " + filter.getClass().getSimpleName());
            }
        }

        protected abstract String alterValue(String original);
    }

    private static class EnumerationFilterToSQLConvertor implements FilterToSQLConvertor {

        private final String operator;

        private EnumerationFilterToSQLConvertor(String operator) {
            this.operator = operator;
        }

        @Override
        public void convert(StringBuilder builder, BaseFilter<?, ?, ?> filter, List<String> selectionArgs) {
            if (filter instanceof EnumerationFilter<?, ?, ?>) {
                EnumerationFilter<?, ?, ?> enumerationFilter = (EnumerationFilter<?, ?, ?>) filter;
                Property<?, ?> property = FilterMethodPublicRouter.getProperty(filter);
                Iterable<?> values = FilterMethodPublicRouter.getValueEnumeration(enumerationFilter);

                builder.append(" ").append(property.getSafeName()).append(" ").append(operator).append(" (");

                String delimiter = "";
                for (Object value : values) {
                    // FIXME this conversion should happen elsewhere!
                    if (property.getType() == Boolean.class) {
                        Boolean booleanValue = (Boolean) value;
                        selectionArgs.add(booleanValue != null && booleanValue ? "1" : "0");
                    } else {
                        selectionArgs.add(value.toString());
                    }
                    builder.append(delimiter).append("?");
                    if(delimiter.equals("")) {
                        delimiter = ", ";
                    }
                }
                builder.append(") ");

            } else {
                Log.w(TAG, "Filter ignored, because it has wrong binding! Expected " +
                        EnumerationFilter.class.getSimpleName() + " but got " + filter.getClass().getSimpleName());
            }
        }
    }

}
