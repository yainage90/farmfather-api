package com.farmfather.farmfatherapi.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
	private String id;
	private String email;
	private String nickName;
	private String introduce;
	private String imageUrl;
}
