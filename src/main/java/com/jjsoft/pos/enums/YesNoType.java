package com.jjsoft.pos.enums;

/**
 * Y/N 공통 enum
 */
public enum YesNoType {

      Y("예")
    , N("아니오")
    ;

    private final String description;

    YesNoType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static YesNoType fromKey(String key) {
        for (YesNoType type : YesNoType.values()) {
            if (type.name().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 YesNoType이 없습니다: " + key);
    }
}
