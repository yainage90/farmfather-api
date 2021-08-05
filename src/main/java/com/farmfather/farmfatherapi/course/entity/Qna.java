package com.farmfather.farmfatherapi.course.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Qna {
	private String id;
	private String title;
	private String writerId;
	private String writerNickName;
	private String question;
	private String created;
	private String updated;
}
