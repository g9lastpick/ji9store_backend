package com.jjsoft.pos.enums;

/**
 * QR 대상 픽업 상태
 */
public enum PickupQrTargetStatus {

      WAIT("대기")
    , READY("픽업가능")
    , DONE("픽업완료")
    , EXPIRED("기간종료")
    ;

    private final String description;

    PickupQrTargetStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static PickupQrTargetStatus fromDescription(String description) {
        for (PickupQrTargetStatus status : PickupQrTargetStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 PickupQrTargetStatus가 없습니다: " + description);
    }

    public static PickupQrTargetStatus fromKey(String key) {
        for (PickupQrTargetStatus status : PickupQrTargetStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrTargetStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (PickupQrTargetStatus status : PickupQrTargetStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrTargetStatus가 없습니다: " + key);
    }
}
