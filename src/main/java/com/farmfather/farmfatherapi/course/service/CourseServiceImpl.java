package com.farmfather.farmfatherapi.course.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.farmfather.farmfatherapi.cloud.s3.service.S3Service;
import com.farmfather.farmfatherapi.course.entity.Course;
import com.farmfather.farmfatherapi.course.entity.Qna;
import com.farmfather.farmfatherapi.course.entity.Rating;
import com.farmfather.farmfatherapi.utils.EsRequestFactory;
import com.google.gson.Gson;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

	@Value("${elasticsearch.index.course.name}")
	private String COURSE_INDEX;

	@Value("${cloud.aws.s3.bucket.course_thumbnail}")
	private String COURSE_THUMBNAIL_BUCKET;

	private final RestHighLevelClient esClient;
	private final S3Service s3Service;


	@Autowired
	public CourseServiceImpl(RestHighLevelClient esClient, S3Service s3Service) {
		this.esClient = esClient;
		this.s3Service = s3Service;
	}

	@Override
	public String delete(String id) {
		DeleteRequest request = EsRequestFactory.createDeleteByIdRequest(COURSE_INDEX, id);

		try {
			esClient.delete(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return id;
	}

	@Override
	public List<Course> getAll(int page) {
		String[] fieldsToInclude =
				new String[] {"id", "title", "thumbnail", "starAvg", "mentorId", "status"};
		String[] fieldsToExclude = new String[] {"chapters", "ratings", "qnas", "numRating", "price",
				"register", "created", "updated"};
		SearchRequest request =
				EsRequestFactory.createSearchAllRequest(COURSE_INDEX, fieldsToInclude, fieldsToExclude);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), Course.class))
				.filter(course -> course.getStatus().equals("ready")).collect(Collectors.toList());

	}

	@Override
	public Course getById(String id) {
		GetRequest request = EsRequestFactory.createGetRequest(COURSE_INDEX, id);
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

		Course course = new Gson().fromJson(response.getSourceAsString(), Course.class);

		return course;
	}

	@Override
	public List<Course> getFavoriteCourses(List<String> ids) {
		String[] includes = new String[] {"id", "title", "starAvg", "status", "thumbnail", "mentorId"};
		String[] excludes = new String[] {"chapters", "qnas", "ratings", "subTitle", "learns",
				"numRating", "detail", "created", "updated"};

		MultiGetRequest request =
				EsRequestFactory.createMultiGetRequest(COURSE_INDEX, ids, includes, excludes);
		MultiGetResponse response;
		try {
			response = esClient.mget(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getResponses())
				.map(item -> new Gson().fromJson(item.getResponse().getSourceAsString(), Course.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<Course> getMyCourses(String mentorId) {
		SearchRequest request =
				EsRequestFactory.createSearchByFieldRequest(COURSE_INDEX, "mentorId", mentorId);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), Course.class))
				.collect(Collectors.toList());
	}

	@Override
	public Course save(String title, String mentorId) {
		log.info("title=" + title + ", mentorId=" + mentorId);
		Course course = new Course();
		course.setId(UUID.randomUUID().toString());
		course.setTitle(title);
		course.setMentorId(mentorId);
		course.setPrice(0);
		course.setNumRating(0);
		course.setStarAvg(0.0f);
		course.setStatus("pending");
		String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		course.setCreated(current);

		IndexRequest request =
				EsRequestFactory.createIndexRequest(COURSE_INDEX, course.getId(), course);

		try {
			esClient.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured", e);
			return null;
		}

		return getById(course.getId());
	}

	@Override
	public Course update(Course course) {

		log.info("course to update=" + course.toString());

		String script = "";
		Map<String, Object> params = new HashMap<>();

		if (course.getTitle() != null) {
			params.put("title", course.getTitle());
			script += "ctx._source.title = params.title;";
		}

		if (course.getStatus() != null) {
			params.put("status", course.getStatus());
			script += "ctx._source.status = params.status;";
		}

		if (course.getLearns() != null) {
			params.put("learns", course.getLearns());
			script += "ctx._source.learns = params.learns;";
		}

		if (course.getPrice() != 0) {
			params.put("price", course.getPrice());
			script += "ctx._source.price = params.price;";
		}

		if (course.getDetail() != null) {
			params.put("detail", course.getDetail());
			script += "ctx._source.detail = params.detail;";
		}

		if (course.getThumbnail() != null) {
			params.put("thumbnail", course.getThumbnail());
			script += "ctx._source.thumbnail = params.thumbnail;";
		}

		if (course.getChapters() != null) {
			params.put("chapters", course.getChapters().stream().map(chapter -> chapter.toMap())
					.collect(Collectors.toList()));
			script += "ctx._source.chapters = params.chapters;";
		}

		if (course.getRatings() != null) {
			params.put("ratings", course.getRatings());
			script += "ctx._source.ratings = params.ratings;";
		}

		if (course.getQnas() != null) {
			params.put("qnas", course.getQnas());
			script += "ctx._source.qnas = params.qnas;";
		}

		String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		params.put("updated", current);
		script += "ctx._source.updated = params.updated;";

		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, course.getId(), inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return getById(course.getId());
	}

	@Override
	public String uploadThumbnail(String id, MultipartFile thumbnailImage) {
		String thumbnail = s3Service.uploadImage(COURSE_THUMBNAIL_BUCKET, id, thumbnailImage);
		log.info("image uploaded to s3 bucket. url=" + thumbnail);

		String script = "";
		Map<String, Object> params = new HashMap<>();

		params.put("thumbnail", thumbnail);
		script += "ctx._source.thumbnail = params.thumbnail;";

		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, id, inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return thumbnail;
	}

	@Override
	public Rating addRating(String courseId, Rating rating) {

		rating.setId(UUID.randomUUID().toString());

		Map<String, Object> params = new HashMap<>();
		params.put("rating", rating);

		String script = "if(ctx._source.ratings == null) {ctx._source.ratings = new ArrayList();}"
				+ "ctx._source.ratings.add(params.rating);";
		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, courseId, inline);

		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return rating;
	}

	@Override
	public String deleteRating(String courseId, String id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);

		String script = "ctx._source.ratings.removeIf(rating -> rating.id == params.id);";
		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, courseId, inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return id;
	}

	@Override
	public Qna addQna(String courseId, Qna qna) {
		qna.setId(UUID.randomUUID().toString());

		Map<String, Object> params = new HashMap<>();
		params.put("qna", qna);

		String script = "if(ctx._source.qnas == null) {ctx._source.qnas = new ArrayList();}"
				+ "ctx._source.qnas.add(params.qna);";
		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, courseId, inline);

		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return qna;
	}

	@Override
	public String deleteQna(String courseId, String id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);

		String script = "ctx._source.qnas.removeIf(qna -> qna.id == params.id);";
		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request =
				EsRequestFactory.createUpdateWithScriptRequest(COURSE_INDEX, courseId, inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return id;
	}
}
