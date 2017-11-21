/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package io.candice.parser.ast.stmt.dal;

import com.baidu.hsb.parser.ast.fragment.Limit;
import com.baidu.hsb.parser.visitor.SQLASTVisitor;

/**
 * @author xiongzhao@baidu.com
 */
public class ShowWarnings extends DALShowStatement {
    private final boolean count;
    private final Limit limit;

    public ShowWarnings(boolean count, Limit limit) {
        this.count = count;
        this.limit = limit;
    }

    public boolean isCount() {
        return count;
    }

    public Limit getLimit() {
        return limit;
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}
