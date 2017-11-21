package io.candice.parser.ast.expression.primary.function.groupby;

import io.candice.parser.ast.expression.Expression;
import io.candice.parser.ast.expression.primary.function.FunctionExpression;
import io.candice.parser.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupConcat extends FunctionExpression {
    private final boolean distinct;
    private final Expression orderBy;
    private final boolean isDesc;
    private final List<Expression> appendedColumnNames;
    private final String separator;

    public GroupConcat(boolean distinct, List<Expression> exprList, Expression orderBy, boolean isDesc,
                       List<Expression> appendedColumnNames, String separator) {
        super("GROUP_CONCAT", exprList);
        this.distinct = distinct;
        this.orderBy = orderBy;
        this.isDesc = isDesc;
        if (appendedColumnNames == null || appendedColumnNames.isEmpty()) {
            this.appendedColumnNames = Collections.emptyList();
        } else if (appendedColumnNames instanceof ArrayList) {
            this.appendedColumnNames = appendedColumnNames;
        } else {
            this.appendedColumnNames = new ArrayList<Expression>(appendedColumnNames);
        }
        this.separator = separator == null ? "," : separator;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Expression getOrderBy() {
        return orderBy;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public List<Expression> getAppendedColumnNames() {
        return appendedColumnNames;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        throw new UnsupportedOperationException("function of char has special arguments");
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}
