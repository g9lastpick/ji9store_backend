package com.jjsoft.pos.enums;

/**
 * 영수증 타입
 */
public enum ReceiptType {

      SALE("매출")
    , CANCEL("취소")
    ;

    private final String description;

    ReceiptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static ReceiptType fromDescription(String description) {
        for (ReceiptType type : ReceiptType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 ReceiptType이 없습니다: " + description);
    }

    public static ReceiptType fromKey(String key) {
        for (ReceiptType type : ReceiptType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 ReceiptType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (ReceiptType type : ReceiptType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 ReceiptType이 없습니다: " + key);
    }
}
