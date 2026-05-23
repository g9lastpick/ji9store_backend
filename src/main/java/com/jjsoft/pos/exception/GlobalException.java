package com.jjsoft.pos.exception;

import com.jjsoft.pos.enums.ResponseCode;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int statusCode;
    private final String errorMessage;

    // 생성자
    public GlobalException(ResponseCode resCode) {
        super(resCode.getMessage());
        this.statusCode = resCode.getCode();
        this.errorMessage = resCode.getMessage();
    }
    public GlobalException(ResponseCode resCode , String msg) {
    	super(resCode.getMessage());
    	this.statusCode = resCode.getCode();
    	this.errorMessage = msg;
    }

}