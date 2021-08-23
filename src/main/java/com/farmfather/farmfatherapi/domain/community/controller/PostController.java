package com.farmfather.farmfatherapi.domain.community.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.farmfather.farmfatherapi.domain.community.entity.Post;
import com.farmfather.farmfatherapi.domain.community.service.PostService;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

	private final PostService postService;

	@GetMapping("/{id}")
	public ResponseEntity<Post> getById(@PathVariable String id) {
		return ResponseEntity.ok(postService.getById(id));
	}

	@GetMapping("/all")
	public ResponseEntity<List<Post>> getAll(@RequestParam String category, @RequestParam int page) {
		return ResponseEntity.ok(postService.getAll(category, page));
	}

	@GetMapping("/my")
	public ResponseEntity<List<Post>> getAll(@RequestParam int page, HttpServletRequest request) {
		String writerId = request.getAttribute("id").toString();
		return ResponseEntity.ok(postService.getByWriterId(writerId, page));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Post>> search(@RequestParam String category,
			@RequestParam String field, @RequestParam String query, @RequestParam int page) {
		return ResponseEntity.ok(postService.search(category, field, query, page));
	}

	@PostMapping("/save")
	public ResponseEntity<String> save(@RequestBody Post post, HttpServletRequest request) {
		String writerId = request.getAttribute("id").toString();
		post.setWriterId(writerId);
		return ResponseEntity.ok(postService.save(post));
	}

	@PutMapping("/update")
	public ResponseEntity<String> update(@RequestBody Post post) {
		return ResponseEntity.ok(postService.update(post));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable String id) {
		return ResponseEntity.ok(postService.delete(id));
	}
}
