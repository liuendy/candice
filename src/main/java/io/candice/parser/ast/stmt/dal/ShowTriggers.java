/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package io.candice.parser.ast.stmt.dal;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.primary.Identifier;
import com.baidu.hsb.parser.visitor.SQLASTVisitor;

/**
 * @author xiongzhao@baidu.com
 */
public class ShowTriggers extends DALShowStatement {
    private Identifier schema;
    private final String pattern;
    private final Expression where;

    public ShowTriggers(Identifier schema, String pattern) {
        this.schema = schema;
        this.pattern = pattern;
        this.where = null;
    }

    public ShowTriggers(Identifier schema, Expression where) {
        this.schema = schema;
        this.pattern = null;
        this.where = where;
    }

    public ShowTriggers(Identifier schema) {
        this.schema = schema;
        this.pattern = null;
        this.where = null;
    }

    public void setSchema(Identifier schema) {
        this.schema = schema;
    }

    public Identifier getSchema() {
        return schema;
    }

    public String getPattern() {
        return pattern;
    }

    public Expression getWhere() {
        return where;
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}
