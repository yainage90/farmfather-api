package com.farmfather.farmfatherapi.domain.course.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Course {

	private String id;
	private String title;
	private String mentorId;
	private List<String> learns;
	private String status;
	private int numRating;
	private float starAvg;
	private int price;
	private int register;
	private String thumbnail;
	private String detail;
	private String created;
	private String updated;
	private List<Chapter> chapters;
	private List<Rating> ratings;
	private List<Qna> qnas;
}
