//package com.nowcoder.community.config;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//
//public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        ClientConfiguration configuration = ClientConfiguration.builder(
//        ).connectedTo("127.0.0.1")
//        //.withConnectTimeout(Duration.ofSeconds(5))
//        //.withSocketTimeout(Duration.ofSeconds(3))
//        //.useSsl()
//        //.withDefaultHeaders(defaultHeaders)
//        //.withBasicAuth(username, password)
//        // ... other options
//        .build();
//        RestHighLevelClient client = RestClients.create(configuration).rest();
//        return client;
//    }
//}
