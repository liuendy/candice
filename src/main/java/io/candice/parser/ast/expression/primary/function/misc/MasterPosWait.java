/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package io.candice.parser.ast.expression.primary.function.misc;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.primary.function.FunctionExpression;

import java.util.List;

/**
 * @author xiongzhao@baidu.com
 */
public class MasterPosWait extends FunctionExpression {
    public MasterPosWait(List<Expression> arguments) {
        super("MASTER_POS_WAIT", arguments);
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        return new MasterPosWait(arguments);
    }

}