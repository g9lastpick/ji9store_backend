package com.jjsoft.pos.enums;

/**
 * 픽업 QR 세션 상태
 */
public enum PickupQrStatus {

      ACTIVE("활성")
    , PARTIAL_DONE("부분 완료")
    , DONE("완료")
    , EXPIRED("만료")
    , REVOKED("관리자 취소")
    ;

    private final String description;

    PickupQrStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static PickupQrStatus fromDescription(String description) {
        for (PickupQrStatus status : PickupQrStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 PickupQrStatus가 없습니다: " + description);
    }

    public static PickupQrStatus fromKey(String key) {
        for (PickupQrStatus status : PickupQrStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (PickupQrStatus status : PickupQrStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PickupQrStatus가 없습니다: " + key);
    }
}
