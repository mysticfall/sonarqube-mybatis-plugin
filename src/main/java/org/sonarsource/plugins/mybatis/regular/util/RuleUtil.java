package org.sonarsource.plugins.mybatis.regular.util;

import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.apache.commons.lang.StringUtils;
import org.sonarsource.plugins.mybatis.regular.enums.RuleCodeEnum;
import org.sonarsource.plugins.mybatis.regular.pojo.XmlPluginRuleResult;
import org.sonarsource.plugins.mybatis.xml.XmlParseResult;
import org.sonarsource.plugins.mybatis.xml.consts.Constant;
import org.sonarsource.plugins.mybatis.xml.consts.ErrorCodeEnum;
import org.sonarsource.plugins.mybatis.xml.pojo.XmlNodeParserResult;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleUtil {

    public static XmlPluginRuleResult doRuleUpdateNoWait(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }

        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        Matcher matcher = Pattern.compile("\\s*for\\s*update\\s*([a-zA-Z]*)").matcher(sql);
        if (matcher.find()) {
            String word = matcher.group(1);
            if (word.isEmpty() || !"wait".equalsIgnoreCase(word)) {
                return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "FOR UPDATE must specify WAIT.");
            }
            return null;
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleDuplicatedId(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        boolean hasDuplicated = xmlNodeParserResult.isHasDuplicated();
        if (hasDuplicated) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Mapper statement ID is duplicated.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleDuplicatedSqlTagId(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        boolean hasDuplicatedSqlTagId = xmlNodeParserResult.isHasDuplicatedSqlTagId();
        if (hasDuplicatedSqlTagId) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Mapper <sql id='xxx'> fragment ID is duplicated.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleExistSubSqlTagId(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        boolean existSubSqlTagId = xmlNodeParserResult.isExistSubSqlTagId();
        if (!existSubSqlTagId) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "The referenced refid does not exist.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleExistSubSqlTagIdDuplicated(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        boolean existSubSqlTagIdDuplicated = xmlNodeParserResult.isExistSubSqlTagIdDuplicated();
        if (existSubSqlTagIdDuplicated) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "The referenced refid is duplicated.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleSelectStar(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (null == xmlNodeParserResult.getException()) {
            SchemaStatVisitor visitor = xmlNodeParserResult.getVisitor();
            String formatSql = xmlNodeParserResult.getFormatSql();
            boolean containSelectAll = DruidUtil.isContainSelectAll(visitor, formatSql);
            if (containSelectAll) {
                return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Do not use SELECT *.");
            }
            return null;
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleForbidden$(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();

        Matcher matcher = Pattern.compile("\\$\\{([a-zA-Z0-9]*)}").matcher(sql);
        if (matcher.find()) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Do not use ${} for parameters; use #{} instead.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleNoConditionInUpdateDelete(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        String nodeOptType = xmlNodeParserResult.getNodeOptType();
        boolean isSuccess = ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(xmlNodeParserResult.getStatusCode());
        boolean isUpdateDelete = "update".equalsIgnoreCase(nodeOptType) || "delete".equalsIgnoreCase(nodeOptType);
        boolean isWhereConditon = sql.contains(Constant.WHERE);
        boolean isJoinConditon = sql.contains("join");
        boolean isWhenConditon = sql.contains(Constant.WHEN);
        if (isSuccess && isUpdateDelete && !isWhereConditon && !isJoinConditon && !isWhenConditon) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                    "UPDATE and DELETE statements must have conditions. Without a condition, the statement can update "
                            + "or delete every row in the table.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleUpdateSubQueryMustHaveExists(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        SchemaStatVisitor visitor = xmlNodeParserResult.getVisitor();
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        String nodeOptType = xmlNodeParserResult.getNodeOptType();
        Matcher matcher = Pattern.compile("\\s*begin.*end").matcher(sql);
        if (matcher.find()) {
            return null;
        }
        boolean isContainSubQuery = false;
        if ("update".equalsIgnoreCase(nodeOptType) && null != visitor) {
            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            if (null != tables && tables.size() > 1) {
                Iterator<Map.Entry<TableStat.Name, TableStat>> it = tables.entrySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Map.Entry<TableStat.Name, TableStat> entry = it.next();
                    entry.getKey().getName();
                    TableStat tableStat = entry.getValue();
                    if (tableStat.getSelectCount() > 0) {
                        isContainSubQuery = true;
                        break;
                    }
                }
            }
            if (isContainSubQuery && !sql.contains("exists")) {
                return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                        "Positive example:\nUPDATE t1 SET t1.a = (SELECT t2.b FROM t2 WHERE t1.id = t2.id)\n"
                                + "WHERE EXISTS(SELECT 1 FROM t2 WHERE t1.id = t2.id)\n"
                                + "If t2 has no row matching t1.id = t2.id, t1.a will be assigned NULL without the EXISTS guard.");
            }
            return null;
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleNoConditionInSelect(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        if (xmlParseResult.getXmlNodeParserResult().isHasDuplicated()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        String nodeOptType = xmlNodeParserResult.getNodeOptType();
        boolean isSelect = "select".equalsIgnoreCase(nodeOptType);
        boolean isWhereConditon = sql.contains(Constant.WHERE);
        boolean isJoinConditon = sql.contains("join");
        if (isSelect && !isWhereConditon && !isJoinConditon) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                    "SELECT statements must have conditions. A SELECT without conditions can cause a full table scan.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleJoinMustOn(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        boolean isJoinConditon = sql.contains("join");
        boolean isOn = sql.contains("on");
        if (isJoinConditon && !isOn) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                    "JOIN operations must have ON conditions to avoid Cartesian products.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleForbiddenTrigger(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        boolean isUseTrigger = sql.contains("trigger");
        if (isUseTrigger) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Do not use database triggers.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleInsertMustHaveColumns(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        SchemaStatVisitor visitor = xmlNodeParserResult.getVisitor();
        String nodeOptType = xmlNodeParserResult.getNodeOptType();
        if ("insert".equalsIgnoreCase(nodeOptType) && null != visitor) {
            Collection<TableStat.Column> columns = visitor.getColumns();
            if (null == columns || columns.isEmpty()) {
                return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                        "INSERT statements must specify an explicit column list.");
            }
            return null;
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleIfTest(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String optType = xmlNodeParserResult.getNodeOptType();
        if (!"select".equalsIgnoreCase(optType) && xmlNodeParserResult.isContainIfTest()) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                    "Write statements should not use if-test dynamic tags. Prefer one SQL statement per business "
                            + "operation instead of a broad update endpoint, and avoid updating unchanged columns.");
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleTableMoreThan5(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        SchemaStatVisitor visitor = xmlNodeParserResult.getVisitor();
        if (null != visitor && null != visitor.getTables()) {
            Map<TableStat.Name, TableStat> tableStatMap = visitor.getTables();
            int size = tableStatMap.size();
            if (size > 5) {
                return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, "Avoid joining more than five tables.");
            }
            return null;
        }
        return null;
    }

    public static XmlPluginRuleResult doRuleNullCompare(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        Matcher matcher1 = Pattern.compile("=\\s*null").matcher(sql);
        Matcher matcher2 = Pattern.compile("null\\s*=").matcher(sql);
        Matcher matcher3 = Pattern.compile("null\\s*!\\s*=").matcher(sql);
        Matcher matcher4 = Pattern.compile("null\\s*<\\s*>").matcher(sql);
        Matcher matcher5 = Pattern.compile("\\s*<\\s*>\\s*null").matcher(sql);
        if (matcher1.find() || matcher2.find() || matcher3.find() || matcher4.find() || matcher5.find()) {
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum,
                    "Compare NULL values with IS NULL or IS NOT NULL.");
        }
        return null;
    }

    public static XmlPluginRuleResult getGrammarResult(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        String parseResultOrSuggestion;
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        if (StringUtils.isEmpty(xmlNodeParserResult.getFormatSql())) {
            return null;
        }
        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        Exception exception = xmlNodeParserResult.getException();
        if (exception instanceof ParserException) {
            ParserException druidParseException = (ParserException) exception;
            if (sql.contains("$")) {
                parseResultOrSuggestion = "The $ symbol cannot be parsed safely and may indicate SQL injection risk; use # instead.";
            } else if (sql.contains("--")) {
                parseResultOrSuggestion = "The SQL may contain an invalid comment (--); use standard MyBatis comment tags.";
            } else {
                parseResultOrSuggestion = druidParseException.getMessage();
            }
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, parseResultOrSuggestion);
        }
        return null;
    }

    public static XmlPluginRuleResult getParseError(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum) {
        String parseResultOrSuggestion;
        if (!ruleCodeEnum.isActive()) {
            return null;
        }
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
//        String sql = xmlNodeParserResult.getFormatSql().toLowerCase();
        Exception exception = xmlNodeParserResult.getException();
        if (exception != null) {
            parseResultOrSuggestion = "SQL parsing failed. Check whether the SQL is valid. Details: " + xmlNodeParserResult.getErrorMsg();
            return createXmlPluginRuleResult(xmlParseResult, ruleCodeEnum, parseResultOrSuggestion);
        }
        return null;
    }

    public static XmlPluginRuleResult createXmlPluginRuleResult(XmlParseResult xmlParseResult, RuleCodeEnum ruleCodeEnum, String parseResultOrSuggestion) {
        XmlNodeParserResult xmlNodeParserResult = xmlParseResult.getXmlNodeParserResult();
        return XmlPluginRuleResult.builder()
                .mapperName(xmlParseResult.getMapperName())
                .sqlNodeId(xmlNodeParserResult.getSqlNodeId()).
                sqlNodeIdOrg(xmlNodeParserResult.getSqlNodeIdOrg())
                .sqlText(xmlNodeParserResult.getFormatSql())
                .nodeOptType(xmlNodeParserResult.getNodeOptType())
                .druidFormatSql(xmlNodeParserResult.getDruidFormatSql())
                .parseResult(parseResultOrSuggestion)
                .ruleCodeEnum(ruleCodeEnum)
                .dbType(xmlNodeParserResult.getDbType())
                .filePath(xmlNodeParserResult.getXmlFilePath())
                .lineNumber(xmlNodeParserResult.getXmlFilePath())
                .build();
    }
}
