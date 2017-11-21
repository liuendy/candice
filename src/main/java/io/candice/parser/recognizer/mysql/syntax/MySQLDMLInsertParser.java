/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package io.candice.parser.recognizer.mysql.syntax;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.misc.QueryExpression;
import com.baidu.hsb.parser.ast.expression.primary.Identifier;
import com.baidu.hsb.parser.ast.expression.primary.RowExpression;
import com.baidu.hsb.parser.ast.stmt.dml.DMLInsertStatement;
import com.baidu.hsb.parser.recognizer.mysql.lexer.MySQLLexer;
import com.baidu.hsb.parser.util.Pair;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.baidu.hsb.parser.recognizer.mysql.MySQLToken.*;

/**
 * @author xiongzhao@baidu.com
 */
public class MySQLDMLInsertParser extends MySQLDMLInsertReplaceParser {
    public MySQLDMLInsertParser(MySQLLexer lexer, MySQLExprParser exprParser) {
        super(lexer, exprParser);
    }

    /**
     * nothing has been pre-consumed <code><pre>
     * 'INSERT' ('LOW_PRIORITY'|'DELAYED'|'HIGH_PRIORITY')? 'IGNORE'? 'INTO'? tbname 
     *  (  'SET' colName ('='|':=') (expr|'DEFAULT') (',' colName ('='|':=') (expr|'DEFAULT'))*
     *   | '(' (  colName (',' colName)* ')' ( ('VALUES'|'VALUE') value (',' value)*
     *                                        | '(' 'SELECT' ... ')'
     *                                        | 'SELECT' ...  
     *                                       )
     *          | 'SELECT' ... ')' 
     *         )
     *   |('VALUES'|'VALUE') value  ( ',' value )*
     *   | 'SELECT' ...
     *  )
     * ( 'ON' 'DUPLICATE' 'KEY' 'UPDATE' colName ('='|':=') expr ( ',' colName ('='|':=') expr)* )?
     * 
     * value := '(' (expr|'DEFAULT') ( ',' (expr|'DEFAULT'))* ')'
     * </pre></code>
     */
    public DMLInsertStatement insert() throws SQLSyntaxErrorException {
        match(KW_INSERT);
        DMLInsertStatement.InsertMode mode = DMLInsertStatement.InsertMode.UNDEF;
        boolean ignore = false;
        switch (lexer.token()) {
        case KW_LOW_PRIORITY:
            lexer.nextToken();
            mode = DMLInsertStatement.InsertMode.LOW;
            break;
        case KW_DELAYED:
            lexer.nextToken();
            mode = DMLInsertStatement.InsertMode.DELAY;
            break;
        case KW_HIGH_PRIORITY:
            lexer.nextToken();
            mode = DMLInsertStatement.InsertMode.HIGH;
            break;
        }
        if (lexer.token() == KW_IGNORE) {
            ignore = true;
            lexer.nextToken();
        }
        if (lexer.token() == KW_INTO) {
            lexer.nextToken();
        }
        Identifier table = identifier();
        List<Pair<Identifier, Expression>> dupUpdate;
        List<Identifier> columnNameList;
        List<RowExpression> rowList;
        QueryExpression select;

        List<Expression> tempRowValue;
        switch (lexer.token()) {
        case KW_SET:
            lexer.nextToken();
            columnNameList = new LinkedList<Identifier>();
            tempRowValue = new LinkedList<Expression>();
            for (;; lexer.nextToken()) {
                Identifier id = identifier();
                match(OP_EQUALS, OP_ASSIGN);
                Expression expr = exprParser.expression();
                columnNameList.add(id);
                tempRowValue.add(expr);
                if (lexer.token() != PUNC_COMMA) {
                    break;
                }
            }
            rowList = new ArrayList<RowExpression>(1);
            rowList.add(new RowExpression(tempRowValue));
            dupUpdate = onDuplicateUpdate();
            return new DMLInsertStatement(mode, ignore, table, columnNameList, rowList, dupUpdate);
        case IDENTIFIER:
            if (!"VALUE".equals(lexer.stringValueUppercase())) {
                break;
            }
        case KW_VALUES:
            lexer.nextToken();
            columnNameList = null;
            rowList = rowList();
            dupUpdate = onDuplicateUpdate();
            return new DMLInsertStatement(mode, ignore, table, columnNameList, rowList, dupUpdate);
        case KW_SELECT:
            columnNameList = null;
            select = select();
            dupUpdate = onDuplicateUpdate();
            return new DMLInsertStatement(mode, ignore, table, columnNameList, select, dupUpdate);
        case PUNC_LEFT_PAREN:
            switch (lexer.nextToken()) {
            case PUNC_LEFT_PAREN:
            case KW_SELECT:
                columnNameList = null;
                select = selectPrimary();
                match(PUNC_RIGHT_PAREN);
                dupUpdate = onDuplicateUpdate();
                return new DMLInsertStatement(mode, ignore, table, columnNameList, select, dupUpdate);
            }
            columnNameList = idList();
            match(PUNC_RIGHT_PAREN);
            switch (lexer.token()) {
            case PUNC_LEFT_PAREN:
            case KW_SELECT:
                select = selectPrimary();
                dupUpdate = onDuplicateUpdate();
                return new DMLInsertStatement(mode, ignore, table, columnNameList, select, dupUpdate);
            case KW_VALUES:
                lexer.nextToken();
                break;
            default:
                matchIdentifier("VALUE");
            }
            rowList = rowList();
            dupUpdate = onDuplicateUpdate();
            return new DMLInsertStatement(mode, ignore, table, columnNameList, rowList, dupUpdate);
        }
        throw err("unexpected token for insert: " + lexer.token());
    }

    /**
     * @return null for not exist
     */
    private List<Pair<Identifier, Expression>> onDuplicateUpdate() throws SQLSyntaxErrorException {
        if (lexer.token() != KW_ON) {
            return null;
        }
        lexer.nextToken();
        matchIdentifier("DUPLICATE");
        match(KW_KEY);
        match(KW_UPDATE);
        List<Pair<Identifier, Expression>> list;
        Identifier col = identifier();
        match(OP_EQUALS, OP_ASSIGN);
        Expression expr = exprParser.expression();
        if (lexer.token() == PUNC_COMMA) {
            list = new LinkedList<Pair<Identifier, Expression>>();
            list.add(new Pair<Identifier, Expression>(col, expr));
            for (; lexer.token() == PUNC_COMMA;) {
                lexer.nextToken();
                col = identifier();
                match(OP_EQUALS, OP_ASSIGN);
                expr = exprParser.expression();
                list.add(new Pair<Identifier, Expression>(col, expr));
            }
            return list;
        }
        list = new ArrayList<Pair<Identifier, Expression>>(1);
        list.add(new Pair<Identifier, Expression>(col, expr));
        return list;
    }
}
