package com.jjsoft.pos.enums;

/**
 * 드로우 응모 상태
 */
public enum DrawEntryStatus {

      ENTRY("응모")
    , WIN("당첨")
    , LOSE("미당첨")
    , CANCEL("취소")
    , NOSHOW("미방문")
    ;

    private final String description;

    DrawEntryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static DrawEntryStatus fromDescription(String description) {
        for (DrawEntryStatus status : DrawEntryStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 DrawEntryStatus가 없습니다: " + description);
    }

    public static DrawEntryStatus fromKey(String key) {
        for (DrawEntryStatus status : DrawEntryStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 DrawEntryStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (DrawEntryStatus status : DrawEntryStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 DrawEntryStatus가 없습니다: " + key);
    }
}
