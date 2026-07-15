package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;
import org.sonarsource.plugins.mybatis.sql.Constant;

import java.util.Objects;

public class NoUseDollarRule extends AbstractRule {
    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if (Objects.equals(methodName, "$")) {
            this.addCheckResult(x);
        }
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
        return "Use #{} instead of ${} for parameters";
    }

    @Override
    public String getDescription() {
        return "Do not bind parameters with ${param}. MyBatis substitutes ${} values as raw text, "
                + "which lets user-controlled input change the SQL statement and can lead to SQL injection. "
                + "Use #{param} so MyBatis creates a prepared-statement parameter instead.";
    }

    @Override
    public String getSimpleDescription() {
        return "Use #{} instead of ${} for parameters";
    }
}
