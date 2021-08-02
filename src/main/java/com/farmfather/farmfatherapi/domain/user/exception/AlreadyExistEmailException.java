package com.farmfather.farmfatherapi.domain.user.exception;

public class AlreadyExistEmailException extends RuntimeException {

	public AlreadyExistEmailException(String msg) {
		super(msg);
	}
}
