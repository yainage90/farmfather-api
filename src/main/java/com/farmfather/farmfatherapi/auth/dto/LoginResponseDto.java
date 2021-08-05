package com.farmfather.farmfatherapi.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class LoginResponseDto {
	private final UserResponseDto user;
	private final String jwt;
}
