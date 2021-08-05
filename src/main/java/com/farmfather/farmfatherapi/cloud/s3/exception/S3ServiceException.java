package com.farmfather.farmfatherapi.cloud.s3.exception;

public class S3ServiceException extends RuntimeException {
	public S3ServiceException(String msg) {
		super(msg);
	}
}
