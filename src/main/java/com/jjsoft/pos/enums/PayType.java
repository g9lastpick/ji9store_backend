package com.jjsoft.pos.enums;

/**
 * 공동구매 결제 방식
 */
public enum PayType {

      PRE("선결제")
    , POST("후결제")
    ;

    private final String description;

    PayType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static PayType fromDescription(String description) {
        for (PayType type : PayType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 PayType이 없습니다: " + description);
    }

    public static PayType fromKey(String key) {
        for (PayType type : PayType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PayType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (PayType type : PayType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PayType이 없습니다: " + key);
    }
}
