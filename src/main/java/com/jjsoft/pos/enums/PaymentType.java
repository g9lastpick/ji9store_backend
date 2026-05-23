package com.jjsoft.pos.enums;

public enum PaymentType {
	  CASH("현금")
	, CARD("카드") 
	, POINT("포인트")
	, GRP("복합")
	;
	
	
	private final String description;

	PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();  // enum 값의 이름을 반환 (예: "ACTIVE")
    }
    
    // description 값을 받아서 해당 enum의 name을 반환
    public static PaymentType getPaymentFromDescription(String description) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getDescription().equals(description)) {
                return type;  // enum의 name()을 반환
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 PaymentType이 없습니다: " + description);
    }
    
    public static PaymentType getPaymentFromKey(String key) {
        for (PaymentType type : PaymentType.values()) {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 PaymentType이 없습니다: " + key);
    }
    public static String getPaymentNameFromKey(String key) {
    	for (PaymentType type : PaymentType.values()) {
    		if (type.name().equals(key) || type.getDescription().equals(key)) {
    			return type.getDescription();  
    		}
    		
    	}
    	
    	
    	
    	throw new IllegalArgumentException("해당 key에 맞는 PaymentType이 없습니다: " + key);
    }
}
