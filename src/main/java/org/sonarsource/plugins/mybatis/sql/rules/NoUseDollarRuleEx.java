package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;

import static org.sonarsource.plugins.mybatis.sql.Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX;


public class NoUseDollarRuleEx extends AbstractRule {
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLExpr sqlExpr = x.getRight();
        if (sqlExpr instanceof SQLVariantRefExpr) {
            SQLVariantRefExpr temp = (SQLVariantRefExpr) sqlExpr;
            if (temp.getName().contains("$")) {
                this.addCheckResult(x);
            }
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
        return "Use #{} parameters in comparisons";
    }

    @Override
    public String getDescription() {
        return "Do not compare values using ${param}. MyBatis substitutes ${} values as raw text, "
                + "which lets user-controlled input change the SQL statement and can lead to SQL injection. "
                + "Use #{param} so comparison values are bound as prepared-statement parameters.";
    }

    @Override
    public String getSimpleDescription() {
        return "Use #{} parameters in comparisons";
    }
}
