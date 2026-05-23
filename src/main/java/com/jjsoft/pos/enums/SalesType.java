package com.jjsoft.pos.enums;

public enum SalesType {
	VISIT("방문")
	,DELIVERY("배달")
	,PACKING("포장")
	,RESERVATION("예약")
	;
	
	private final String description;

	SalesType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();  // enum 값의 이름을 반환 (예: "ACTIVE")
    }
    
    // description 값을 받아서 해당 enum의 name을 반환
    public static SalesType getSalesTypeFromDescription(String description) {
        for (SalesType type : SalesType.values()) {
            if (type.getDescription().equals(description)) {
                return type;  // enum의 name()을 반환
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 SalesType이 없습니다: " + description);
    }
    
    public static SalesType getSalesTypeFromKey(String key) {
        for (SalesType type : SalesType.values() ) 
        {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 SalesType이 없습니다: " + key);
    }
    public static String getSalesTypeNameFromKey(String key) {
    	for (SalesType type : SalesType.values() )
    	{
    		if (type.name().equals(key) || type.getDescription().equals(key)) {
    			return type.getDescription();  
    		}
    	}
    	throw new IllegalArgumentException("해당 key에 맞는 SalesType이 없습니다: " + key);
    }
}
