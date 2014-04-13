package com.sun.codemodel;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class JAssign {

    public static JAssignment assign(JAssignmentTarget lhs, JExpression rhs) {
        return new JAssignment(lhs, rhs);
    }

    public static JAssignment assign(JAssignmentTarget lhs, JExpression rhs, String op) {
        return new JAssignment(lhs, rhs, op);
    }

}
