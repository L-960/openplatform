package com.lxy.openplatform.search.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// es配置中心
@Configuration
public class ESConfig {

    @Value("${spring.data.elasticsearch.host}")
    private String host;

    @Value("${spring.data.elasticsearch.port}")
    private int port;

    @Value("${spring.data.elasticsearch.connectionTimeOut}")
    private int connectionTimeOut;

    @Value("${spring.data.elasticsearch.socketTimeOut}")
    private int socketTimeOut;

    @Value("${spring.data.elasticsearch.connectionRequestTimeOut}")
    private int connectionRequestTimeOut;

    @Value("${spring.data.elasticsearch.maxConnectNum}")
    private int maxConnectNum;

    @Value("${spring.data.elasticsearch.maxConnectNumPerRoute}")
    private int maxConnectNumPerRoute;


    @Bean
    public RestHighLevelClient restHighLevelClient() {

        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(host, port));

        clientBuilder.setRequestConfigCallback((builder -> {
                    builder.setConnectTimeout(connectionTimeOut);
                    builder.setSocketTimeout(socketTimeOut);
                    builder.setConnectionRequestTimeout(connectionRequestTimeOut);
                    return builder;
                })
        );

        clientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            httpAsyncClientBuilder.setMaxConnTotal(maxConnectNum);
            httpAsyncClientBuilder.setMaxConnPerRoute(maxConnectNumPerRoute);
            return httpAsyncClientBuilder;
        });

        return new RestHighLevelClient(clientBuilder);
    }
}
