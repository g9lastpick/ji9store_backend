package com.jjsoft.pos.enums;

public enum SalesStatus {
	COMPLETE("완료")
	,CANCEL("취소")
	,RETURN("반품")
	,RESERVATION("예약")
	;
	
	private final String description;

	SalesStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();  // enum 값의 이름을 반환 (예: "ACTIVE")
    }
    
    // description 값을 받아서 해당 enum의 name을 반환
    public static SalesStatus getSalesStatusFromDescription(String description) {
        for (SalesStatus type : SalesStatus.values()) {
            if (type.getDescription().equals(description)) {
                return type;  // enum의 name()을 반환
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 SalesStatus이 없습니다: " + description);
    }
    
    public static SalesStatus getSalesStatusFromKey(String key) {
        for (SalesStatus type : SalesStatus.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 SalesStatus이 없습니다: " + key);
    }
    
    public static String getSalesStatusNameFromKey(String key) {
        for (SalesStatus type : SalesStatus.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type.getDescription();  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 SalesStatus이 없습니다: " + key);
    }
}
