package org.sonarsource.plugins.mybatis.xml.consts;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ErrorCodeEnum {
    SUCCESS("200000", "Success"),
    E500001("E500001", "System error"),
    E500002("E500002", "SQL may contain potential problems"),
    E500003("E500003", "Unable to get the XML node ID"),
    E500004("E500004", "Unsupported XML node type"),
    E500006("E500006", "No handler class is registered for the node tag"),
    E500007("E500007", "Duplicated refid <sql id>"),
    E500008("E500008", "Duplicated node ID"),
    E500009("E500009", "The refid does not exist"),
    E500010("E500010", "The refid is duplicated"),
    E888888("E888888", "Rule violation"),
    E999991("E999991", "iBATIS support is incomplete and currently unsupported"),
    E999999("E999999", "XML must be MyBatis or iBATIS");

    private static final Logger log = LoggerFactory.getLogger(ErrorCodeEnum.class);
    private final String code;
    private final String desc;

    ErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ErrorCodeEnum parse(String code) {
        ErrorCodeEnum[] values;
        for (ErrorCodeEnum item : values()) {
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

    @Override // java.lang.Enum
    public String toString() {
        return this.code + ":" + this.desc;
    }
}
