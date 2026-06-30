package com.vita.vitapickBack.jwtToken;

public class CustomJWTException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CustomJWTException(String msg){
	      super(msg);
	}
}
