package com.farmfather.farmfatherapi.domain.user.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserResponseDto {

	private String id;
	private String username;// email
	private String nickName;
	private String introduce;
	private String imageUrl;
	private List<String> favoriteCourses;
}
