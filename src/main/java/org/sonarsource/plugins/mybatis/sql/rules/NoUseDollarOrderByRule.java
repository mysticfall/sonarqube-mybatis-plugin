package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;

import static org.sonarsource.plugins.mybatis.sql.Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX;

public class NoUseDollarOrderByRule extends AbstractRule {
    @Override
    public boolean visit(SQLSelectOrderByItem x) {
        if (x.getExpr() instanceof SQLVariantRefExpr) {
            SQLVariantRefExpr temp = (SQLVariantRefExpr) x.getExpr();
            String name = temp.getName() + "";
            if (name.contains("$")) {
                this.addCheckResult(temp);
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
        return "Do not use raw parameters in ORDER BY clauses";
    }

    @Override
    public String getDescription() {
        return "Do not pass ORDER BY expressions through ${param}. MyBatis substitutes ${} values as raw text, "
                + "which lets user-controlled input change the SQL statement and can lead to SQL injection. "
                + "Use a whitelist of allowed column names and sort directions before constructing dynamic ordering.";
    }

    @Override
    public String getSimpleDescription() {
        return "Do not use raw parameters in ORDER BY clauses";
    }
}
