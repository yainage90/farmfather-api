package com.farmfather.farmfatherapi.domain.user.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;
import com.farmfather.farmfatherapi.domain.user.exception.AlreadyExistEmailException;
import com.farmfather.farmfatherapi.utils.EsRequestFactory;
import com.google.gson.Gson;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final RestHighLevelClient esClient;

	@Value("${elasticsearch.index.user.name}")
	private String USER_INDEX;

	@Override
	public CustomUserDetails getById(String id) {
		GetRequest request = EsRequestFactory.createGetRequest(USER_INDEX, id);
		GetResponse getResponse;
		try {
			getResponse = esClient.get(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}
		return new Gson().fromJson(getResponse.getSourceAsString(), CustomUserDetails.class);
	}

	@Override
	public CustomUserDetails getByEmail(String email) {
		SearchRequest request = EsRequestFactory.createSearchByFieldRequest(USER_INDEX, "email", email);

		SearchResponse response;
		try {
			response = esClient.search(request, RequestOptions.DEFAULT);
			log.info("total hits: " + response.getHits().getTotalHits());
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		List<CustomUserDetails> retrived = Arrays.stream(response.getHits().getHits())
				.map(hit -> new Gson().fromJson(hit.getSourceAsString(), CustomUserDetails.class))
				.collect(Collectors.toList());

		if (retrived.size() > 0) {
			return retrived.get(0);
		}

		return null;
	}

	@Override
	public CustomUserDetails changeNickName(String id, String nickName) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("nickName", nickName);

		String script = "ctx._source.nickName = params.nickName";
		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request = EsRequestFactory.createUpdateWithScriptRequest(USER_INDEX, id, inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return getById(id);
	}

	@Override
	public String deleteCustomUserDetails(String id) {
		DeleteRequest request = EsRequestFactory.createDeleteByIdRequest(USER_INDEX, id);
		DeleteResponse response;
		try {
			response = esClient.delete(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}

		return response.getId();
	}

	@Override
	public CustomUserDetails save(CustomUserDetails user) throws AlreadyExistEmailException {

		CustomUserDetails originUser = getByEmail(user.getUsername());
		if (originUser != null) {
			throw new AlreadyExistEmailException("동일한 email이 존재합니다.");
		}

		user.setId(UUID.randomUUID().toString());
		IndexRequest request = EsRequestFactory.createIndexRequest(USER_INDEX, user.getId(), user);
		try {
			esClient.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return user;
	}
}
