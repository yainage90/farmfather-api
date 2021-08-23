package com.farmfather.farmfatherapi.domain.community.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {
	private String id;
	private String category;
	private String title;
	private String content;
	private String writerId;
	private String writerNickName;
	private String created;
	private String updated;
}
