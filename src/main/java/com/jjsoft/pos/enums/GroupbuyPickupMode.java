package com.jjsoft.pos.enums;

/**
 * 공동구매 픽업 시작 설정
 *  - MANUAL : 관리자가 픽업 시작/종료 일시 직접 지정
 *  - AUTO   : 첫 급간(최소수량) 달성 시점부터 당일 20시까지 자동 픽업
 */
public enum GroupbuyPickupMode {

      MANUAL("수동")
    , AUTO("자동")
    ;

    private final String description;

    GroupbuyPickupMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();
    }
}
