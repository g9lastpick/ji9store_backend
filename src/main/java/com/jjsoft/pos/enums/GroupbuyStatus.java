package com.jjsoft.pos.enums;

/**
 * 공동구매 진행 상태
 */
public enum GroupbuyStatus {

      READY("대기")
    , START("진행중")
    , SUCCESS("성공")
    , FAIL("실패")
    , CANCEL("취소")
    , END("종료")
    ;

    private final String description;

    GroupbuyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static GroupbuyStatus fromDescription(String description) {
        for (GroupbuyStatus status : GroupbuyStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 GroupbuyStatus가 없습니다: " + description);
    }

    public static GroupbuyStatus fromKey(String key) {
        for (GroupbuyStatus status : GroupbuyStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (GroupbuyStatus status : GroupbuyStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyStatus가 없습니다: " + key);
    }
}
