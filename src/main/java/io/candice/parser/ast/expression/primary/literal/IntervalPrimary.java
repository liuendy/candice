package io.candice.parser.ast.expression.primary.literal;

import io.candice.parser.ast.expression.Expression;
import io.candice.parser.visitor.SQLASTVisitor;

import java.util.HashMap;
import java.util.Map;

public class IntervalPrimary extends Literal {
    public static enum Unit {
        MICROSECOND,
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        QUARTER,
        YEAR,
        SECOND_MICROSECOND,
        MINUTE_MICROSECOND,
        MINUTE_SECOND,
        HOUR_MICROSECOND,
        HOUR_SECOND,
        HOUR_MINUTE,
        DAY_MICROSECOND,
        DAY_SECOND,
        DAY_MINUTE,
        DAY_HOUR,
        YEAR_MONTH
    }

    private static final Map<String, Unit> unitMap = initUnitMap();

    private static Map<String, Unit> initUnitMap() {
        Unit[] units = Unit.class.getEnumConstants();
        Map<String, Unit> map = new HashMap<String, Unit>(units.length);
        for (Unit unit : units) {
            map.put(unit.name(), unit);
        }
        return map;
    }

    /**
     * @param unitString must be upper case, null is forbidden
     */
    public static Unit getIntervalUnit(String unitString) {
        return unitMap.get(unitString);
    }

    private final Unit unit;
    private final Expression quantity;

    public IntervalPrimary(Expression quantity, Unit unit) {
        super();
        if (quantity == null) throw new IllegalArgumentException("quantity expression is null");
        if (unit == null) throw new IllegalArgumentException("unit of time is null");
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * @return never null
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * @return never null
     */
    public Expression getQuantity() {
        return quantity;
    }

    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}
