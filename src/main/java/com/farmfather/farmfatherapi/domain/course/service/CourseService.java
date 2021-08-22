package com.farmfather.farmfatherapi.domain.course.service;

import java.util.List;
import com.farmfather.farmfatherapi.domain.course.entity.Course;
import com.farmfather.farmfatherapi.domain.course.entity.Qna;
import com.farmfather.farmfatherapi.domain.course.entity.Rating;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {

	Course getById(String id);

	List<Course> getAll(int page);

	List<Course> getFavoriteCourses(List<String> ids);

	List<Course> getMyCourses(String mentorId);

	Course save(String title, String mentorId);

	Course update(Course course);

	String uploadThumbnail(String id, MultipartFile thumbnailImage);

	String delete(String id);

	Rating addRating(String courseId, Rating rating);

	String deleteRating(String courseId, String id);

	Qna addQna(String courseId, Qna qna);

	String deleteQna(String courseId, String id);
}
