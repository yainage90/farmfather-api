package com.farmfather.farmfatherapi.domain.community.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.farmfather.farmfatherapi.domain.community.entity.Post;
import com.farmfather.farmfatherapi.utils.EsRequestFactory;
import com.google.gson.Gson;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

	@Value("${elasticsearch.index.post.name}")
	private String POST_INDEX;

	private final RestHighLevelClient esClient;

	@Autowired
	public PostServiceImpl(RestHighLevelClient esClient) {
		this.esClient = esClient;
	}

	@Override
	public List<Post> getAll(String category, int page) {

		SearchRequest request = EsRequestFactory.createSearchByFieldRequest(POST_INDEX, "category",
				category, 10 * page, 10);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), Post.class))
				.filter(post -> post.getCategory().equals(category)).collect(Collectors.toList());
	}

	@Override
	public Post getById(String id) {
		GetRequest request = EsRequestFactory.createGetRequest(POST_INDEX, id);
		GetResponse response;
		try {
			response = esClient.get(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		if (!response.isExists()) {
			return null;
		}

		Post post = new Gson().fromJson(response.getSourceAsString(), Post.class);

		return post;
	}

	@Override
	public List<Post> getByWriterId(String userId, int page) {

		SearchRequest request =
				EsRequestFactory.createSearchByFieldRequest(POST_INDEX, "writerId", userId, 10 * page, 10);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), Post.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<Post> search(String category, String field, String query, int page) {

		log.info("category=" + category + ", field=" + field + ", query=" + query + ", page=" + page);

		SearchRequest request = new SearchRequest(POST_INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.termQuery(field, query));
		boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(10 * page);
		searchSourceBuilder.size(10);

		request.source(searchSourceBuilder);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), Post.class))
				.collect(Collectors.toList());
	}

	@Override
	public String save(Post post) {
		log.info(post.toString());
		post.setId(UUID.randomUUID().toString());
		String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		post.setCreated(current);
		post.setUpdated(current);

		IndexRequest request = EsRequestFactory.createIndexRequest(POST_INDEX, post.getId(), post);

		try {
			esClient.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured", e);
			return null;
		}

		return post.getId();
	}

	@Override
	public String update(Post post) {
		log.info("post to update=" + post.toString());

		String script = "";
		Map<String, Object> params = new HashMap<>();

		params.put("category", post.getCategory());
		script += "ctx._source.category = params.category;";

		params.put("title", post.getTitle());
		script += "ctx._source.title = params.title;";

		params.put("content", post.getContent());
		script += "ctx._source.content = params.content;";

		String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		params.put("updated", current);
		script += "ctx._source.updated = params.updated;";

		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(POST_INDEX, post.getId(), inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return post.getId();
	}

	@Override
	public String delete(String id) {
		DeleteRequest request = EsRequestFactory.createDeleteByIdRequest(POST_INDEX, id);

		try {
			esClient.delete(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return id;
	}
}
