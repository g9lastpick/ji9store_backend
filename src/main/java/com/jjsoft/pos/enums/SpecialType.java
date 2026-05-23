package com.jjsoft.pos.enums;

public enum SpecialType {
	 EVENT("이벤트")
	,ETC("기타 이벤트")
	;
	
	private final String description;

	SpecialType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();  // enum 값의 이름을 반환 (예: "ACTIVE")
    }
    
    // description 값을 받아서 해당 enum의 name을 반환
    public static SpecialType getSpecialTypeFromDescription(String description) {
        for (SpecialType type : SpecialType.values()) {
            if (type.getDescription().equals(description)) {
                return type;  // enum의 name()을 반환
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 SpecialType이 없습니다: " + description);
    }
    
    public static SpecialType getSpecialTypeFromKey(String key) {
        for (SpecialType type : SpecialType.values() ) 
        {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 SpecialType이 없습니다: " + key);
    }
    public static String getSpecialTypeNameFromKey(String key) {
    	for (SpecialType type : SpecialType.values() )
    	{
    		if (type.name().equals(key) || type.getDescription().equals(key)) {
    			return type.getDescription();  
    		}
    	}
    	throw new IllegalArgumentException("해당 key에 맞는 SpecialType이 없습니다: " + key);
    }
}
