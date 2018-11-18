package com.hai.elk;

import com.hai.elk.common.config.InitElasticSearchConfig;
import com.hai.model.Book;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ElasticsearchTest {

    @Autowired
    private InitElasticSearchConfig jestClientConfig;

    @Before
    public void before() {
        if (null == jestClientConfig) {
            jestClientConfig = new InitElasticSearchConfig();
        }
    }

    private TransportClient buildTransportClient() {
        try {
            Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
            TransportClient client = new PreBuiltTransportClient(settings, DeleteByQueryPlugin.class)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9200));
            return client;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void indexBook() {
        Book book = new Book();
        book.setId(2);
        book.setName("ElasticSearch权威指南");
        book.setPrice(79.99);
        book.setDescription("Elasticsearch是一个实时分布式搜索和分析引擎。它让你以前所未有的速度处理大数据成为可能");

        Index index = new Index.Builder(book)
                .index("books")
                .type("book")
                .id("1")
                .build();

        try {
            jestClientConfig.getClient().execute(index);
            System.out.println("index book finished!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchSourceBuilder() {
        //使用SearchSourceBuilder来组建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //在description域上查询匹配Elasticsearch的所有文档
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "Elasticsearch"));
        Search search = new Search.Builder(searchSourceBuilder.toString())
                //可以添加多个index和type
                .addIndex("books")
                .addType("book")
                .build();
        try {
            SearchResult result = jestClientConfig.getClient().execute(search);
            List<Book> books = result.getSourceAsObjectList(Book.class, false);
            books.stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteByQuery() {
        TransportClient client = buildTransportClient();

        DeleteRequest deleteRequest = new DeleteRequest("index", "type", "id");
        ActionFuture<DeleteResponse> deleteResponseActionFuture = client.delete(deleteRequest);

        DeleteResponse deleteResponse = deleteResponseActionFuture.actionGet();
        RestStatus restStatus = deleteResponse.status();

        int status = restStatus.getStatus();
        System.out.println("delete by query rest status for delete response: " + status);
    }

    //reference: https://www.ibm.com/developerworks/library/j-use-elasticsearch-java-apps/index.html
    @Test
    public void testSaveWithSpark() {
        TransportClient client = buildTransportClient();
        Spark.post("/save", (request, response) -> {
            StringBuilder json = new StringBuilder("{");
            json.append("\"name\":\"" + request.raw().getParameter("name") + "\",");
            json.append("\"artist\":\"" + request.raw().getParameter("artist") + "\",");
            json.append("\"year\":" + request.raw().getParameter("year") + ",");
            json.append("\"album\":\"" + request.raw().getParameter("album") + "\",");
            json.append("\"lyrics\":\"" + request.raw().getParameter("lyrics") + "\"}");

            IndexRequest indexRequest = new IndexRequest("music", "lyrics",
                    UUID.randomUUID().toString());
            indexRequest.source(json.toString());
            IndexResponse esResponse = client.index(indexRequest).actionGet();

            RestStatus restStatus = esResponse.status();
            int status = restStatus.getStatus();
            System.out.println("delete by query rest status for delete response: " + status);

            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "index.mustache");

        }, new MustacheTemplateEngine());
    }

    @Test
    public void testSearchWithSpark() {
        TransportClient client = buildTransportClient();

        Spark.get("/search", (request, response) -> {
            SearchRequestBuilder srb = client.prepareSearch("music").setTypes("lyrics");
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            srb.highlighter(highlightBuilder.field("lyrics", 0, 0));

            String lyricParam = request.queryParams("query");
            QueryBuilder lyricQuery = null;
            if (lyricParam != null && lyricParam.trim().length() > 0) {
                lyricQuery = QueryBuilders.matchQuery("lyrics", lyricParam);
            }

            String artistParam = request.queryParams("artist");
            QueryBuilder artistQuery = null;
            if (artistParam != null && artistParam.trim().length() > 0) {
                artistQuery = QueryBuilders.matchQuery("artist", artistParam);
            }

            if (lyricQuery != null && artistQuery == null) {
                srb.setQuery(lyricQuery);
            } else if (lyricQuery == null && artistQuery != null) {
                srb.setQuery(artistQuery);
            } else if (lyricQuery != null && artistQuery != null) {
                srb.setQuery(QueryBuilders.boostingQuery(artistQuery, lyricQuery));
            }

            SearchResponse searchResponse = srb.execute().actionGet();
            SearchHit[] hits = searchResponse.getHits().getHits();

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("songs", hits);

            return new ModelAndView(attributes, "index.mustache");

        }, new MustacheTemplateEngine());
//    }, new FreeMarkerEngine());
    }

}
