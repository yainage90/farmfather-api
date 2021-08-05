package com.farmfather.farmfatherapi.auth.exception;

public class AlreadyExistEmailException extends RuntimeException {

	public AlreadyExistEmailException(String msg) {
		super(msg);
	}
}
