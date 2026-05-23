package com.jjsoft.pos.enums;

public enum ProgressType {
	 READY("준비")
	,START("진행중")
	,STOP("종료")
	,CANCEL("취소")
	;
	
	private final String description;

	ProgressType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return name();  // enum 값의 이름을 반환 (예: "ACTIVE")
    }
    
    // description 값을 받아서 해당 enum의 name을 반환
    public static ProgressType getProgressTypeFromDescription(String description) {
        for (ProgressType type : ProgressType.values()) {
            if (type.getDescription().equals(description)) {
                return type;  // enum의 name()을 반환
            }
        }
        throw new IllegalArgumentException("해당 description에 맞는 ProgressType이 없습니다: " + description);
    }
    
    public static ProgressType getProgressTypeFromKey(String key) {
        for (ProgressType type : ProgressType.values() ) 
        {
            if (type.name().equals(key) || type.getDescription().equals(key)) {
                return type;  
            }
        }
        throw new IllegalArgumentException("해당 key에 맞는 ProgressType이 없습니다: " + key);
    }
    public static String getProgressTypeNameFromKey(String key) {
    	for (ProgressType type : ProgressType.values() )
    	{
    		if (type.name().equals(key) || type.getDescription().equals(key)) {
    			return type.getDescription();  
    		}
    	}
    	throw new IllegalArgumentException("해당 key에 맞는 ProgressType이 없습니다: " + key);
    }
}
