package com.farmfather.farmfatherapi.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.LoginResponseDto;
import com.farmfather.farmfatherapi.auth.dto.LogoutResponseDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterResponseDto;
import com.farmfather.farmfatherapi.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	public ResponseEntity<RegisterResponseDto> register(RegisterRequestDto registerRequestDto) {
		log.info("registerRequestDto=" + registerRequestDto);
		RegisterResponseDto response = authService.register(registerRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
		log.info("loginRequestDto=" + loginRequestDto);
		LoginResponseDto response = authService.login(loginRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<LogoutResponseDto> logout(HttpServletRequest httpServletRequest) {
		String jwt = httpServletRequest.getAttribute("userId").toString();
		LogoutResponseDto response = authService.logout(jwt);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
