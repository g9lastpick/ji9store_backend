package com.jjsoft.pos.enums;

public enum ResponseCode {

	  SUCCESS              (200, "Success")
    , BAD_REQUEST          (400, "Bad Request")
    , UNAUTHORIZED         (401, "Unauthorized")
    , NOT_FOUND_OBJECT     (450 , "object not found")            
    , INTERNAL_SERVER_ERROR(500, "Internal Server Error")
    , QUERY_ERROR          (550, "queryError")
    , PRODUCT_DTL_LOT_NO_DUP(600, "상품 상세 : 입고일이 같은 상품이 있습니다")
    , NOT_FOUND_ENTITY     (601, "엔티티를 찾을 수 없습니다. 관리자에게 문의하세요")
    , SAVE_ERROR           (602, "저장 에러 관리자에게 문의하세요")
    , RESERVATION_OVER     (700, "예약가능 수량을 초과했습니다. 수량을 확인해주세요")
    
    , RESERVATION_SAVE_ZERO(800, "저장 할 수량이 없습니다")
    
    , SPECIAL_PRICE_ZERO   (900, "특가 단가가 0일수가 없습니다. 관리자에게 문의하세요")
    , NOT_FOUND            (1000 , "존재 하지 않습니다.")
    
    , IMAGE_UPLOAD_ERR     (1100 , "S3 이미지 업로드 에러")
    
    ;

    private final int code;
    private final String message;

    // 생성자
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 코드 반환
    public int getCode() {
        return code;
    }

    // 메시지 반환
    public String getMessage() {
        return message;
    }
}
