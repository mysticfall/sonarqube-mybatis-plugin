package org.sonarsource.plugins.mybatis.sql.rules;

import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import org.sonar.api.rule.Severity;
import org.sonarsource.plugins.mybatis.sql.AbstractRule;

import static org.sonarsource.plugins.mybatis.sql.Constant.MYBATIS_MATTER_CHECK_RULE_PREFIX;

public class NoUseSelectAllColumnsRule extends AbstractRule {
    @Override
    public boolean visit(SQLAllColumnExpr x) {
        this.addCheckResult(x);
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
        return "Avoid SELECT *";
    }

    @Override
    public String getDescription() {
        return "Do not query all columns with SELECT *. Listing the required columns makes the mapper's data contract "
                + "explicit, avoids retrieving unused data, and reduces the risk of failures or behavior changes when "
                + "the table schema changes.";
    }

    @Override
    public String getSimpleDescription() {
        return "Avoid SELECT *";
    }
}
