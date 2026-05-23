package com.jjsoft.pos.enums;

/**
 * 공동구매 참여 상태
 */
public enum GroupbuyJoinStatus {

      JOIN("참여")
    , PAYED("결제완료")
    , CANCEL("취소")
    , FAIL("실패")
    ,NOSHOW("노쇼")
    ;

    private final String description;

    GroupbuyJoinStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }

    public static GroupbuyJoinStatus fromDescription(String description) {
        for (GroupbuyJoinStatus status : GroupbuyJoinStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 GroupbuyJoinStatus가 없습니다: " + description);
    }

    public static GroupbuyJoinStatus fromKey(String key) {
        for (GroupbuyJoinStatus status : GroupbuyJoinStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status;
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyJoinStatus가 없습니다: " + key);
    }

    public static String getNameFromKey(String key) {
        for (GroupbuyJoinStatus status : GroupbuyJoinStatus.values()) {
            if (status.name().equals(key) || status.getDescription().equals(key)) {
                return status.getDescription();
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 GroupbuyJoinStatus가 없습니다: " + key);
    }
}
