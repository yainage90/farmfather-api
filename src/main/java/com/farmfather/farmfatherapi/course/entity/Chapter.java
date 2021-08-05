package com.farmfather.farmfatherapi.course.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Chapter {

	private String id;
	private String title;
	private List<Lecture> lectures;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", getId());
		map.put("title", getTitle());
		map.put("lectures",
				lectures.stream().map(lecture -> lecture.toMap()).collect(Collectors.toList()));

		return map;
	}
}
