package com.javas.analysis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Slf4j
@PropertySource("application.properties")
public class ElasticsearchClientConfig {

    @Value("${elasticsearch.host}")
    private String server;

    @Value("${elasticsearch.port}")
    private String port;

    @Bean
    public RestClient restClientBuild() {
        RestClientBuilder builder = null;
        try {
            HttpHost host = new HttpHost(server, Integer.parseInt(port), "http");
            builder = RestClient.builder(host);
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                    return builder.setConnectTimeout(5000)
                                  .setSocketTimeout(5000)
                                  .setConnectionRequestTimeout(5000);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return builder.build();
    }

}
