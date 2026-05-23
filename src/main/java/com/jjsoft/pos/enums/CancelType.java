package com.jjsoft.pos.enums;

/**
 * 취소 타입
 */
public enum CancelType {

      PART("부분취소")
    , ALL("전체취소")
    ;

    private final String description;

    CancelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static CancelType fromDescription(String description) {
        for (CancelType type : CancelType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 CancelType이 없습니다: " + description);
    }

    public static CancelType fromKey(String key) {
        for (CancelType type : CancelType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 CancelType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (CancelType type : CancelType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 CancelType이 없습니다: " + key);
    }
}
