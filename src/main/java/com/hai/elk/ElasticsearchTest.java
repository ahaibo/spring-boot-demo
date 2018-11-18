package com.hai.elk;

import com.hai.elk.common.config.InitElasticSearchConfig;
import com.hai.model.Book;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ElasticsearchTest {

    @Autowired
    private InitElasticSearchConfig jestClientConfig;

    @Before
    public void before() {
        if (null == jestClientConfig) {
            jestClientConfig = new InitElasticSearchConfig();
        }
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
        try {
            Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
            TransportClient client = new PreBuiltTransportClient(settings, DeleteByQueryPlugin.class)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9200));

            DeleteRequest deleteRequest = new DeleteRequest("index", "type", "id");
            ActionFuture<DeleteResponse> deleteResponseActionFuture = client.delete(deleteRequest);

            DeleteResponse deleteResponse = deleteResponseActionFuture.actionGet();
            RestStatus restStatus = deleteResponse.status();

            int status = restStatus.getStatus();
            System.out.println("delete by query rest status for delete response: " + status);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
