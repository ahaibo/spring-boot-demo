package com.hai.elk.common.config;

import com.google.gson.GsonBuilder;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;


/**
 * 初始化es
 */
public class InitElasticSearchConfig {

    private static final String ES_URL = "http://localhost:9200";
    private JestClient client;

    public JestClient getClient() {
        return client;
    }

    public InitElasticSearchConfig() {
        client = getClientConfig(ES_URL);
    }

    public InitElasticSearchConfig(String esUrl) {
        client = getClientConfig(esUrl);
    }

    public JestClient getClientConfig(String esUrl) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(esUrl)
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
                .multiThreaded(true)
                .readTimeout(10000)
                .build());
        return factory.getObject();
    }

}