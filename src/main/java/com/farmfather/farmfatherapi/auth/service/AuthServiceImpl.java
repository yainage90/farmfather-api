package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.LoginResponseDto;
import com.farmfather.farmfatherapi.auth.dto.LogoutResponseDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterResponseDto;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		
	}

	@Override
	public LogoutResponseDto logout(String jwt) {
		return null;
	}

	@Override
	public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
		return null;
	}

	private boolean isValidPassword(LoginRequestDto loginRequestDto) {

	}
}
