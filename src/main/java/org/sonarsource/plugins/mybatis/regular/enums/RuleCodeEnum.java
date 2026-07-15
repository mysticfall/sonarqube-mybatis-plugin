package org.sonarsource.plugins.mybatis.regular.enums;

import org.apache.commons.lang.StringUtils;

public enum RuleCodeEnum {
    R0001(true, DegreeEnum.BLOCKED, "FOR UPDATE must specify WAIT"),
    R0002(true, DegreeEnum.BLOCKED, "Mapper statement IDs must be unique"),
    R0003(true, DegreeEnum.BLOCKED, "Mapper SQL fragment IDs must be unique"),
    R0004(true, DegreeEnum.BLOCKED, "Referenced refid must exist"),
    R0005(true, DegreeEnum.BLOCKED, "Referenced refid must not be duplicated"),
    //    R1001(true, DegreeEnum.CRITICAL, "Avoid SELECT *"),
//    R1002(true, DegreeEnum.CRITICAL, "Use #{} instead of ${} for parameters"),
    R1003(true, DegreeEnum.CRITICAL, "UPDATE and DELETE statements must have conditions"),
    R1004(true, DegreeEnum.CRITICAL, "UPDATE subqueries should use EXISTS to avoid assigning NULL when rows do not match"),
    R2001(true, DegreeEnum.MAJOR, "Avoid if-test dynamic tags in write statements"),
    R2002(true, DegreeEnum.MAJOR, "Avoid joining more than five tables"),
    R2003(true, DegreeEnum.MAJOR, "Do not compare NULL directly"),
    R2004(true, DegreeEnum.MAJOR, "SELECT statements must have conditions"),
    R2005(true, DegreeEnum.MAJOR, "JOIN operations must have ON conditions"),
    R2006(true, DegreeEnum.MAJOR, "Do not use database triggers"),
    R2007(true, DegreeEnum.MAJOR, "INSERT statements must specify a column list"),
    R3000(true, DegreeEnum.MAJOR, "SQL parsing failed; check whether the SQL is valid"),
    R9999(true, DegreeEnum.MAJOR, "Possible syntax problem or risk");

    private final String desc;
    private boolean active;
    private DegreeEnum degreeEnum;

    RuleCodeEnum(boolean active, DegreeEnum degreeEnum, String desc) {
        this.active = active;
        this.degreeEnum = degreeEnum;
        this.desc = desc;
    }

    public static RuleCodeEnum parse(String code) {
        RuleCodeEnum[] values;
        for (RuleCodeEnum item : values()) {
            if (StringUtils.equals(code, item.name())) {
                return item;
            }
        }
        throw new RuntimeException("Enum code not exist.");
    }

    public String getName() {
        return this.name();
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public DegreeEnum getDegreeEnum() {
        return this.degreeEnum;
    }

    public void setDegreeEnum(DegreeEnum degreeEnum) {
        this.degreeEnum = degreeEnum;
    }
}
