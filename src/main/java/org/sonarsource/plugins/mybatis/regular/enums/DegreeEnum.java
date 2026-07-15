package org.sonarsource.plugins.mybatis.regular.enums;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rule.Severity;

public enum DegreeEnum {
    BLOCKED(Severity.BLOCKER, "Blocker"),
    CRITICAL(Severity.CRITICAL, "Critical"),
    MINOR(Severity.MINOR, "Minor"),
    MAJOR(Severity.MAJOR, "Major");

    private final String code;
    private final String desc;

    DegreeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DegreeEnum parse(String code) {
        DegreeEnum[] values;
        for (DegreeEnum item : values()) {
            if (StringUtils.equals(code, item.code)) {
                return item;
            }
        }
        throw new RuntimeException("Enum code not exist.");
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
