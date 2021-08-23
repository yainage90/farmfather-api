package com.farmfather.farmfatherapi.domain.community.service;

import java.util.List;
import com.farmfather.farmfatherapi.domain.community.entity.Post;

public interface PostService {

	Post getById(String id);

	List<Post> getAll(String category, int page);

	List<Post> getByWriterId(String userId, int page);

	List<Post> search(String category, String field, String query, int page);

	String save(Post post);

	String update(Post post);

	String delete(String id);
}
