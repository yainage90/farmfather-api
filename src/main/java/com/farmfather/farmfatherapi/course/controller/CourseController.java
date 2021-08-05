package com.farmfather.farmfatherapi.course.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.farmfather.farmfatherapi.course.entity.Course;
import com.farmfather.farmfatherapi.course.entity.Qna;
import com.farmfather.farmfatherapi.course.entity.Rating;
import com.farmfather.farmfatherapi.course.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

	private final CourseService courseService;

	@GetMapping("/{id}")
	public ResponseEntity<Course> getById(@PathVariable String id) {
		return ResponseEntity.ok(courseService.getById(id));
	}

	@GetMapping("/all")
	public ResponseEntity<List<Course>> getAll() {
		return ResponseEntity.ok(courseService.getAll(0));
	}

	@GetMapping("/favorite/{ids}")
	public ResponseEntity<List<Course>> getFavoriteCourses(@PathVariable List<String> ids) {
		return ResponseEntity.ok(courseService.getFavoriteCourses(ids));
	}

	@GetMapping("/my")
	public ResponseEntity<List<Course>> getMyCourses(HttpServletRequest request) {
		String mentorId = request.getAttribute("id").toString();
		return ResponseEntity.ok(courseService.getMyCourses(mentorId));
	}

	@PostMapping("/save")
	public ResponseEntity<Course> save(@RequestBody Course course, HttpServletRequest request) {
		String mentorId = request.getAttribute("id").toString();
		return ResponseEntity.ok(courseService.save(course.getTitle(), mentorId));
	}

	@PutMapping("/update")
	public ResponseEntity<Course> update(@RequestBody Course course) {
		return ResponseEntity.ok(courseService.update(course));
	}

	@PostMapping("/{id}/thumbnail")
	public ResponseEntity<String> uploadThumbnail(@PathVariable String id,
			@RequestParam MultipartFile thumbnailImage) {
		return ResponseEntity.ok(courseService.uploadThumbnail(id, thumbnailImage));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable String id) {
		return ResponseEntity.ok(courseService.delete(id));
	}

	@PutMapping("/{courseId}/rating/save")
	public ResponseEntity<Rating> saveRating(@PathVariable String courseId,
			@RequestBody Rating rating) {
		return ResponseEntity.ok(courseService.addRating(courseId, rating));
	}

	@DeleteMapping("/{courseId}/rating/delete/{id}")
	public ResponseEntity<String> deleteRating(@PathVariable String courseId,
			@PathVariable String id) {
		return ResponseEntity.ok(courseService.deleteRating(courseId, id));
	}

	@PutMapping("/{courseId}/qna/save")
	public ResponseEntity<Qna> saveQna(@PathVariable String courseId, @RequestBody Qna qna) {
		return ResponseEntity.ok(courseService.addQna(courseId, qna));
	}

	@DeleteMapping("/{courseId}/qna/delete/{id}")
	public ResponseEntity<String> deleteQna(@PathVariable String courseId, @PathVariable String id) {
		return ResponseEntity.ok(courseService.deleteQna(courseId, id));
	}
}
