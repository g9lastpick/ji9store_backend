package com.jjsoft.pos.enums;

/**
 * 드로우 이벤트 상태
 */
public enum DrawStatus {

      READY("대기")
    , ENTRY("응모중")
    , DRAW("추첨중")
    , COMPLETE("완료")
    , CANCEL("취소")
    ;

    private final String description;

    DrawStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static DrawStatus fromDescription(String description) {
        for (DrawStatus status : DrawStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 DrawStatus가 없습니다: " + description);
    }

    public static DrawStatus fromKey(String key) {
        for (DrawStatus status : DrawStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 DrawStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (DrawStatus status : DrawStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 DrawStatus가 없습니다: " + key);
    }
}
