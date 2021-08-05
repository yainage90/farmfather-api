package com.farmfather.farmfatherapi.course.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {

	private String id;
	private String writerId;
	private String writerNickName;
	private int star;
	private String comment;
	private String created;
	private String updated;
}
