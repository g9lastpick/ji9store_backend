package com.jjsoft.pos.enums;

/**
 * QR 대상 타입
 */
public enum PickupQrTargetType {

      SPECIAL("특가")
    , GROUPBUY("공동구매")
    ;

    private final String description;

    PickupQrTargetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static PickupQrTargetType fromDescription(String description) {
        for (PickupQrTargetType type : PickupQrTargetType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 PickupQrTargetType이 없습니다: " + description);
    }

    public static PickupQrTargetType fromKey(String key) {
        for (PickupQrTargetType type : PickupQrTargetType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrTargetType이 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (PickupQrTargetType type : PickupQrTargetType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrTargetType이 없습니다: " + key);
    }
}
