package com.farmfather.farmfatherapi.cloud.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String uploadImage(String bucketName, String fileName, MultipartFile imageFile);

	boolean deleteImage(String bucketName, String fileName);

	void deleteImagesWithPrefix(String bucketName, String prefix);
}
