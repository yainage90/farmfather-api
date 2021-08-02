package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.domain.user.dto.UserResponseDto;

public interface AuthService {

	public void authenticate(LoginRequestDto loginRequestDto) throws Exception;

	public UserResponseDto register(RegisterRequestDto registerRequestDto);
}
