package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;
import org.sonarsource.plugins.mybatis.sql.Constant;

public class NoUseDollarInListRule extends AbstractRule {
    @Override
    public boolean visit(SQLInListExpr x) {
        // do sth.
        return super.visit(x);
    }

    @Override
    public String getRuleID() {
        return Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX + this.getClass().getSimpleName();
    }

    @Override
    public String getSeverity() {
        return Severity.BLOCKER;
    }

    @Override
    public String getName() {
        return "Use #{} parameters in IN clauses";
    }

    @Override
    public String getDescription() {
        return "Do not build IN clause values with ${param}. MyBatis substitutes ${} values as raw text, "
                + "which lets user-controlled input change the SQL statement and can lead to SQL injection. "
                + "Use #{param} or a foreach collection with #{item} placeholders instead.";
    }

    @Override
    public String getSimpleDescription() {
        return "Use #{} parameters in IN clauses";
    }
}
