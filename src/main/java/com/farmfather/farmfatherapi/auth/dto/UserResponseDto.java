package com.farmfather.farmfatherapi.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserResponseDto {

	private String id;
	private String email;// email
	private String nickName;
	private String introduce;
	private String profile;
	private List<String> favoriteCourses;
}
