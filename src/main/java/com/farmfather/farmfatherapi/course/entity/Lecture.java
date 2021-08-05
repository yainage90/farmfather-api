package com.farmfather.farmfatherapi.course.entity;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Lecture {
	String id;
	String title;
	String videoUrl;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", getId());
		map.put("title", getTitle());
		map.put("videoUrl", getVideoUrl());

		return map;
	}
}
