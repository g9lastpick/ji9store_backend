package com.jjsoft.pos.enums;

/**
 * 공동구매 결과 타입
 */
public enum GroupbuyResultType {

      SUCCESS("성공")
    , FAIL("실패")
    , CANCEL("취소")
    ;

    private final String description;

    GroupbuyResultType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static GroupbuyResultType fromDescription(String description) {
        for (GroupbuyResultType type : GroupbuyResultType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 GroupbuyResultType이 없습니다: " + description);
    }

    public static GroupbuyResultType fromKey(String key) {
        for (GroupbuyResultType type : GroupbuyResultType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyResultType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (GroupbuyResultType type : GroupbuyResultType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyResultType이 없습니다: " + key);
    }
}
