package com.farmfather.farmfatherapi.utils;

import java.util.List;
import com.google.gson.Gson;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

public class EsRequestFactory {
	public static SearchRequest createSearchAllRequest(String indexName) {

		SearchRequest request = new SearchRequest(indexName);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(10000);

		request.source(searchSourceBuilder);

		return request;
	}

	public static SearchRequest createSearchAllRequest(String indexName, String[] fieldsToInclude,
			String[] fieldsToExclude, int from, int size) {

		SearchRequest request = new SearchRequest(indexName);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.from(from);
		searchSourceBuilder.size(size);
		searchSourceBuilder.fetchSource(fieldsToInclude, fieldsToExclude);

		request.source(searchSourceBuilder);

		return request;
	}

	public static GetRequest createGetRequest(String indexName, String id) {
		return new GetRequest(indexName, id);
	}

	public static MultiGetRequest createMultiGetRequest(String indexName, List<String> ids,
			String[] includes, String[] excludes) {

		MultiGetRequest request = new MultiGetRequest();

		final FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);

		ids.stream().forEach(id -> {
			request.add(new MultiGetRequest.Item(indexName, id).fetchSourceContext(fetchSourceContext));
		});

		return request;
	}

	public static IndexRequest createIndexRequest(String indexName, String id, Object object) {
		IndexRequest indexRequest = new IndexRequest(indexName);
		indexRequest.id(id);
		indexRequest.source(new Gson().toJson(object), XContentType.JSON);

		return indexRequest;
	}

	public static UpdateRequest createUpdateRequest(String index, String id, Object object) {
		UpdateRequest request = new UpdateRequest(index, id);
		request.doc(new Gson().toJson(object), XContentType.JSON);

		return request;
	}

	public static DeleteRequest createDeleteByIdRequest(String index, String id) {
		DeleteRequest request = new DeleteRequest(index, id);
		return request;
	}

	public static SearchRequest createSearchByFieldRequest(String index, String field, String value,
			int from, int size) {

		SearchRequest request = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(field, value);
		searchSourceBuilder.query(termQueryBuilder);
		searchSourceBuilder.from(from);
		searchSourceBuilder.size(size);

		request.source(searchSourceBuilder);

		return request;
	}


	public static DeleteByQueryRequest createDeleteByQuerydRequest(String[] indices, String field,
			String value) {

		DeleteByQueryRequest request = new DeleteByQueryRequest(indices);

		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(field, value);
		request.setQuery(termQueryBuilder);

		request.setMaxRetries(3);

		return request;
	}

	public static UpdateRequest createUpdateWithScriptRequest(String index, String id,
			Script inline) {

		UpdateRequest request = new UpdateRequest(index, id);
		request.script(inline);

		return request;

	}
}
