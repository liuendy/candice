package io.candice.parser.ast.expression.bit;

import io.candice.parser.ast.expression.BinaryOperatorExpression;
import io.candice.parser.ast.expression.Expression;
import io.candice.parser.visitor.SQLASTVisitor;

public class BitOrExpression extends BinaryOperatorExpression {
    public BitOrExpression(Expression leftOprand, Expression rightOprand) {
        super(leftOprand, rightOprand, PRECEDENCE_BIT_OR);
    }

    @Override
    public String getOperator() {
        return "|";
    }

    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}
