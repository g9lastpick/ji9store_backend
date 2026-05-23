package com.jjsoft.pos.enums;

/**
 * 공동구매 목표 기준
 */
public enum GroupbuyType {

      QTY("수량")
    , AMOUNT("금액")
    ;

    private final String description;

    GroupbuyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static GroupbuyType fromDescription(String description) {
        for (GroupbuyType type : GroupbuyType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 GroupbuyType이 없습니다: " + description);
    }

    public static GroupbuyType fromKey(String key) {
        for (GroupbuyType type : GroupbuyType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (GroupbuyType type : GroupbuyType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyType이 없습니다: " + key);
    }
}
