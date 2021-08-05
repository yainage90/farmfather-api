package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

	void authenticate(LoginRequestDto loginRequestDto) throws Exception;

	UserResponseDto register(RegisterRequestDto registerRequestDto);

	String uploadProfile(String id, MultipartFile thumbnailImage);
}
