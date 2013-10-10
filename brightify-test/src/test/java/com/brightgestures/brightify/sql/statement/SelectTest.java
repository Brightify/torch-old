package com.brightgestures.brightify.sql.statement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class SelectTest {

    @Test
    public void testCompoundOperator() {
        StringBuilder builder = new StringBuilder();
        Select.CompoundOperator operator = new Select.CompoundOperator(Select.CompoundOperator.Type.UNION);
        operator.query(builder);
        assertEquals("UNION", builder.toString());

        builder = new StringBuilder();
        operator = new Select.CompoundOperator(Select.CompoundOperator.Type.UNION_ALL);
        operator.query(builder);
        assertEquals("UNION ALL", builder.toString());

        builder = new StringBuilder();
        operator = new Select.CompoundOperator(Select.CompoundOperator.Type.INTERSECT);
        operator.query(builder);
        assertEquals("INTERSECT", builder.toString());

        builder = new StringBuilder();
        operator = new Select.CompoundOperator(Select.CompoundOperator.Type.EXCEPT);
        operator.query(builder);
        assertEquals("EXCEPT", builder.toString());
    }

    @Test
    public void completeTest() {

        Select.SelectCore selectCore = new Select.SelectCore();
        selectCore.addResultColumn(Select.ResultColumn.all());
        selectCore.setFrom("test_table");
        selectCore.setWhereExpression("f1=v1 && (f2=v2 || f3=v3)");

        Select.CompoundOperator compoundOperator = new Select.CompoundOperator(Select.CompoundOperator.Type.UNION);

        Select.SelectCore selectCore1 = new Select.SelectCore();
        selectCore1.addResultColumn(Select.ResultColumn.all("test_table2"));
        selectCore1.setFrom("test_table2");
        selectCore1.setWhereExpression("1=1");

        Select selectStatement = new Select(selectCore);
        selectStatement.addSelectCore(compoundOperator, selectCore1);



        StringBuilder builder = new StringBuilder();
        selectStatement.query(builder);
        assertEquals("SELECT * FROM test_table WHERE f1=v1 && (f2=v2 || f3=v3) UNION SELECT test_table2.* FROM test_table2 WHERE 1=1", builder.toString());
    }

}
