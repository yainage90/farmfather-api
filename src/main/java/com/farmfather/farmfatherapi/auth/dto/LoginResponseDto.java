package com.farmfather.farmfatherapi.auth.dto;

import com.farmfather.farmfatherapi.auth.entity.CustomUser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class LoginResponseDto {
	CustomUser user;
	String jwt;
}
