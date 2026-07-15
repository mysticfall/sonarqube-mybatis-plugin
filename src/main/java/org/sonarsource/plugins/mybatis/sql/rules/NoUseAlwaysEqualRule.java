package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;

import static org.sonarsource.plugins.mybatis.sql.Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX;

public class NoUseAlwaysEqualRule extends AbstractRule {
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        if (left.equals(right)) {
            this.addCheckResult(x);
        }
        return super.visit(x);
    }

    @Override
    public String getRuleID() {
        return MYBATIS_MATTER_CHECK_RULE_PREFIX + this.getClass().getSimpleName();
    }

    @Override
    public String getSeverity() {
        return Severity.MAJOR;
    }

    @Override
    public String getName() {
        return "Do not use always-true conditions";
    }

    @Override
    public String getDescription() {
        return "Avoid conditions that compare an expression with itself, such as 'WHERE 2 = 2'. "
                + "If all following dynamic conditions are omitted, the statement may run without an effective filter "
                + "and scan the entire table.";
    }

    @Override
    public String getSimpleDescription() {
        return "Do not use always-true conditions";
    }
}
