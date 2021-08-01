package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.LoginResponseDto;
import com.farmfather.farmfatherapi.auth.dto.LogoutResponseDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterResponseDto;

public interface AuthService {
	
	public RegisterResponseDto register(RegisterRequestDto registerRequestDto);
	public LoginResponseDto login(LoginRequestDto loginRequestDto);
	public LogoutResponseDto logout(String jwt);
}
