package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;

import static org.sonarsource.plugins.mybatis.sql.Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX;

public class NoUseDollarLikeRule extends AbstractRule {
    @Override
    public boolean visit(SQLCharExpr x) {
        String temp = x.getText() + "";
        if (temp.contains("$")) {
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
        return Severity.BLOCKER;
    }

    @Override
    public String getName() {
        return "Use #{} parameters in LIKE clauses";
    }

    @Override
    public String getDescription() {
        return "Do not build LIKE patterns with ${param}. MyBatis substitutes ${} values as raw text, "
                + "which lets user-controlled input change the SQL statement and can lead to SQL injection. "
                + "Bind the value with #{param} and add wildcard characters safely in SQL or application code.";
    }

    @Override
    public String getSimpleDescription() {
        return "Use #{} parameters in LIKE clauses";
    }
}
